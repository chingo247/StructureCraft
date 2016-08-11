/*
 * Copyright (C) 2016 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.structureapi.io.schematic;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.util.XXHasher;
import com.chingo247.structureapi.StructureCraft;
import com.chingo247.structureapi.model.container.FastClipboard;
import com.chingo247.structureapi.model.persistent.schematic.SchematicNode;
import com.chingo247.structureapi.model.persistent.schematic.SchematicRepository;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class SchematicStorage {

    public static final SchematicStorage IMP = new SchematicStorage();

    private final Map<Long, ISchematic> schematics;
    private final long TWO_WEEKS = 1000 * 60 * 60 * 24 * 7 * 2;

    private SchematicStorage() {
        this.schematics = Collections.synchronizedMap(new HashMap<Long, ISchematic>());
    }

    /**
     * Will attempt to get the schematic from the cache. Otherwise the schematic
     * will be loaded and returned
     *
     * @param schematicFile The schematicFile
     * @return The schematic
     */
    public ISchematic getOrLoad(File schematicFile) throws IOException {
        XXHasher hasher = new XXHasher();
        long checksum = hasher.hash64(schematicFile);
        ISchematic schematic = getSchematic(checksum);
        if (schematic == null) {
            FastClipboard clipboard = FastClipboard.read(schematicFile);
            schematic = new Schematic(schematicFile, clipboard.getWidth(), clipboard.getHeight(), clipboard.getLength(), clipboard.getyAxisOffset());
            schematics.put(checksum, schematic);
            clipboard = null;
        }
        return schematic;
    }

    public synchronized ISchematic getSchematic(Long checksum) {
        return schematics.get(checksum);
    }
    
    public synchronized void loadSchematicsFromDirectory(File directory) {
        Preconditions.checkArgument(directory.isDirectory());
        
        Iterator<File> fit = FileUtils.iterateFiles(directory, new String[]{"schematic"}, true);
        
        
        if (fit.hasNext()) {
            GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
            SchematicRepository schematicRepository = new SchematicRepository(graph);
            ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()); // only create the pool if we have schematics
            Map<Long, SchematicNode> alreadyHere = Maps.newHashMap();
            Map<Long, SchematicNode> needsUpdating = Maps.newHashMap();
            
            List<SchematicProcessor> tasks = Lists.newArrayList();
            List<ISchematic> alreadyDone = Lists.newArrayList();
            XXHasher hasher = new XXHasher();

            try (Transaction tx = graph.beginTx()) {
                Collection<SchematicNode> schematicNodes = schematicRepository.findAfterDate(System.currentTimeMillis() - TWO_WEEKS);
                for (SchematicNode node : schematicNodes) {
                    if(!node.hasRotation()) {
                        needsUpdating.put(node.getXXHash64(), node);
                        continue;
                    }
                    alreadyHere.put(node.getXXHash64(), node);
                }

                // Process the schematics that need to be loaded
                while (fit.hasNext()) {
                    File schematicFile = fit.next();
                    try {
                        long checksum = hasher.hash64(schematicFile);
                        // Only load schematic data that wasn't yet loaded...
                        SchematicNode existingData = alreadyHere.get(checksum);
                        if (existingData != null) {
                            ISchematic s = new Schematic(schematicFile, existingData.getWidth(), existingData.getHeight(), existingData.getLength(), existingData.getAxisOffset());
                            alreadyDone.add(s);
                        } else if (getSchematic(checksum) == null) {
                            SchematicProcessor processor = new SchematicProcessor(schematicFile);
                            tasks.add(processor);
                            pool.execute(processor);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(SchematicStorage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                tx.success();
            }

            // Wait for the processes the finish and queue them for bulk insert
            List<ISchematic> newSchematics = Lists.newArrayList();
            try {
                for (SchematicProcessor sp : tasks) {
                    ISchematic schematic = sp.get();
                    if (schematic != null) {
                        newSchematics.add(schematic);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(SchematicStorage.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Close the pool!
            pool.shutdown();

            int updated = 0;
            // Update the database
            try (Transaction tx = graph.beginTx()) {
                for (ISchematic data : alreadyDone) {
                    SchematicNode sdn = schematicRepository.findByHash(data.getHash());
                    sdn.setLastImport(System.currentTimeMillis());
                }
                for (ISchematic newData : newSchematics) {
                    if(needsUpdating.get(newData.getHash()) != null) {
                        SchematicNode dataNode = schematicRepository.findByHash(newData.getHash());
                        dataNode.setRotation(newData.getAxisOffset());
                        updated++;
                        continue;
                    } 
                    String name = newData.getFile().getName();
                    long xxhash = newData.getHash();
                    int width = newData.getWidth();
                    int height = newData.getHeight();
                    int length = newData.getLength();
                    int axisOffset = newData.getAxisOffset();
                    schematicRepository.addSchematic(name, xxhash, width, height, length, axisOffset, System.currentTimeMillis());
                }

                // Delete unused
                int removed = 0;
                for (SchematicNode sdn : schematicRepository.findBeforeDate(System.currentTimeMillis() - TWO_WEEKS)) {
                    sdn.delete();
                    removed++;
                }
                if (removed > 0) {
                    System.out.println("[SettlerCraft]: Deleted " + removed + " schematic(s) from cache");
                }
                
                if (updated > 0) {
                    System.out.println("[SettlerCraft]: Updated " + updated + " schematic(s) from cache");
                }

                tx.success();
            }
            

            synchronized (schematics) {
                for (ISchematic schematic : newSchematics) {
                    schematics.put(schematic.getHash(), schematic);
                }
                for (ISchematic schematic : alreadyDone) {
                    schematics.put(schematic.getHash(), schematic);
                }
            }

        }

    }

}
