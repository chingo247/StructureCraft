/*
 * Copyright (C) 2016 Chingo247
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
package com.chingo247.structurecraft.io.schematic;

import com.chingo247.structurecraft.io.blockstore.NBTUtils;
import com.chingo247.structurecraft.persistence.dao.SchematicDAO;
import com.chingo247.structurecraft.util.value.Tuple2Val;
import com.chingo247.structurecraft.util.XXHasher;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import com.chingo247.structurecraft.services.ServicesManager;
import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.NamedTag;
import java.util.List;

/**
 *
 * @author Chingo247
 */
public class SchematicStorage {

    private LoadingCache<Long, SchematicHeader> cache;

    /**
     * The schematics for old schematics. Structures may be removed but may yet
     * be restored.
     */
    private File oldDir;

    /**
     * The directory where all schematics will be stored.
     */
    private final File schematicsDir;


    /**
     * Constructor.
     *
     * @param schematicsDir The directory the schematics will be copied to
     * @param cachedHeaders Will only cache the header (width, height, length) of the schematic
     */
    public SchematicStorage(File schematicsDir, int cachedHeaders) {
        this.schematicsDir = schematicsDir;
        this.oldDir = new File(schematicsDir, "staged_deleted");
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(cachedHeaders)
                .build(new CacheLoader<Long, SchematicHeader>() {
                    @Override
                    public SchematicHeader load(Long key) throws Exception {
                        // Get the schematic file
                        File schematicFile = getSchematicFile(key);

                        // If we get here... the schematic file MUST exist
                        return Schematic.readHeader(schematicFile);
                    }
                });
    }

    private File getSchematicFile(long hash) throws FileNotFoundException {
        File schematicFile = new File(schematicsDir, String.valueOf(hash) + ".schematic");
        if (!schematicFile.exists()) {
            schematicFile = new File(oldDir, String.valueOf(hash) + ".schematic");
            if (!schematicFile.exists()) {
                throw new FileNotFoundException("Schematic not found for '" + schematicFile.getName() + "'");
            }
        }
        return schematicFile;
    }

    public SchematicHeader getSchematicHeader(long hash) throws ExecutionException {
        return cache.get(hash);
    }

    public ISchematic getSchematic(long hash) throws FileNotFoundException, IOException {
        File schematicFile = getSchematicFile(hash);
        return Schematic.readSchematic(schematicFile, false);
    }

    public boolean hasSchematic(long hash) {
        File schematic = new File(schematicsDir, String.valueOf(hash) + ".schematic");
        if (schematic.exists()) {
            return true;
        }

        File oldSchematic = new File(oldDir, String.valueOf(hash) + ".schematic");
        return oldSchematic.exists();
    }

    /**
     * Imports schematics from a directory.
     *
     * @param directory The directory to start searching
     * @param recursively Search through directories recursively
     * @throws IOException May throw IO Exception when reading or writing
     * schematic files
     */
    public void importSchematics(File directory, boolean recursively) throws IOException {
        Iterable<File> files = () -> FileUtils.iterateFiles(directory, new String[]{"schematic"}, recursively);
        SchematicStorage.this.importSchematics(files, true);
    }

    /**
     * Imports the schematics of the iterable.
     *
     * @param schematics The schematic file iterable
     * @throws IOException May throw IO Exception when reading or writing
     * schematic files
     */
    public void importSchematics(Iterable<File> schematics) throws IOException {
        SchematicStorage.this.importSchematics(schematics, false);
    }

    /**
     * Copies schematics from given directory to workspace directory (e.g.
     * /StructureCraft/Schematics)
     */
    private void importSchematics(Iterable<File> files, boolean isFiltered) throws IOException {
        Iterable<File> filesToImport;
        if (isFiltered) {
            filesToImport = files;
        } else {
            filesToImport = new ArrayList<>();
            for (File f : files) {
                if (FilenameUtils.isExtension(f.getName(), "schematic")) {
                    ((ArrayList<File>) filesToImport).add(f);
                }
            }
        }

        Set<Long> imported = FileUtils.listFiles(schematicsDir, new String[]{"schematic"}, true)
                .parallelStream()
                .map((schematicFile) -> {
                    return Long.parseLong(schematicFile.getName());
                }).collect(Collectors.toSet());

        XXHasher hasher = new XXHasher();
        Map<Long, File> toImport = StreamSupport.stream(filesToImport.spliterator(), true)
                .map((schematicFile) -> {
                    long hash;
                    try {
                        hash = hasher.hash64(schematicFile);
                        return new Tuple2Val<Long, File>(hash, schematicFile);
                    } catch (IOException ex) {
                        Logger.getLogger(SchematicStorage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return null;
                })
                .filter((tuple) -> tuple != null && !imported.contains(tuple.getItem1()))
                .collect(Collectors.toMap(Tuple2Val::getItem1, Tuple2Val::getItem2));

        File schematicReadMe = new File(schematicsDir, "schematicList");

        if (!schematicReadMe.exists()) {
            schematicReadMe.createNewFile();
        }

        toImport.entrySet()
                .parallelStream()
                .forEach((entry) -> {
                    try {
                        Files.copy(entry.getValue(), new File(schematicsDir, entry.getKey().toString()));
                    } catch (IOException ex) {
                        Logger.getLogger(SchematicStorage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

        Map<Long, String> schematicKeyValue = new HashMap<>();
        toImport.entrySet().forEach(x -> {
            schematicKeyValue.put(x.getKey(), x.getValue().getName());
        });

        List<SchematicData> sd = toImport.entrySet()
                .parallelStream()
                .map((entry) -> {
                    try {
                        long hash = entry.getKey();
                        File schematic = new File(schematicsDir, entry.getKey().toString());
                        
                        String title = FilenameUtils.getBaseName(schematic.getName());
                        
                        NamedTag root = Schematic.readRootTag(schematic);
                        byte[] ids = NBTUtils.getChildTag((Map) root.getTag(), "Blocks", ByteArrayTag.class).getValue();

                        int count = 0;
                        for (int i = 0; i < ids.length; i++) {
                            count += ids[i] != 0 ? 1 : 0;
                        }

                        SchematicHeader header = Schematic.readHeader(schematic);
                        return new SchematicData(title, hash, count, header);
                    } catch (IOException ex) {
                        Logger.getLogger(SchematicStorage.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    }
                }).filter(data -> data != null)
                .collect(Collectors.toList());
        
        DBI dbi = ServicesManager.IMP.getDBI();
        try (Handle handle = dbi.open()) {
            SchematicDAO schematicDAO = handle.attach(SchematicDAO.class);
            handle.begin();
            for(SchematicData s : sd) {
                schematicDAO.insert(s.hash, s.title, s.header.getWidth(), s.header.getHeight(), s.header.getLength(), s.blocks);
            }
            handle.commit();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(schematicReadMe))) {
            String s = null;
            while ((s = reader.readLine()) != null) {
                if (s.startsWith("#")) {
                    continue;
                }

                String[] keyValue = s.split("=");
                if (keyValue.length == 2) {
                    schematicKeyValue.put(Long.parseLong(keyValue[1]), keyValue[0]);
                }
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(schematicReadMe))) {
            pw.println("#############################################################");
            pw.println("# This file is generated and contains the original description/name of each schematic file. \n"
                    + "# This directory contains every schematic that is used by a structure. Since 3.0.0 schematics \n"
                    + "# are stored at a central location (and not per structure) to save space. \n"
                    + "# The 'strange' number which is used as name of the schematic files, is a hash value. "
                    + "# This is a unique number based on the contents of the schematic file. \n "
                    + "# \n"
                    + "# NOTE: NEVER CHANGE THE NAMES OF THE CONTENTS OF THIS DIRECTORY. CHANGING THE NAMES OF THE FILES"
                    + "# MAY RESULT IN STRUCTURES NOT BEING ABLE TO LOAD SCHEMATICS");
            pw.println("#############################################################");
            schematicKeyValue.entrySet()
                    .stream()
                    .sorted((Map.Entry<Long, String> o1, Map.Entry<Long, String> o2) -> {
                        return o1.getValue().compareTo(o2.getValue());
                    }).forEach(x -> {
                        pw.printf("%-48s %-3s %-64s", x.getValue(), " = ", String.valueOf(x.getKey()));
                    });
            pw.flush();
        }
    }

    private class SchematicData {

        private String title;
        private long hash;
        private int blocks;
        private SchematicHeader header;

        public SchematicData(String title, long hash, int blocks, SchematicHeader header) {
            this.hash = hash;
            this.blocks = blocks;
            this.header = header;
            this.title = title;
        }

    }

}
