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
package com.chingo247.structureapi.construction;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.concurrent.KeyPool;
import com.chingo247.structureapi.StructureCraft;
import com.chingo247.structureapi.construction.block.BlockSession;
import com.chingo247.structureapi.construction.block.BlockSessionFactory;
import com.chingo247.structureapi.exception.plans.StructurePlanException;
import com.chingo247.structureapi.model.persistent.RelTypes;
import com.chingo247.structureapi.model.persistent.structure.Structure;
import com.chingo247.structureapi.model.persistent.structure.StructureNode;
import com.chingo247.structureapi.model.persistent.structure.StructureRepository;
import com.chingo247.structureapi.util.task.QueueTask;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 * Class for basic construction operations
 * @author Chingo
 */
public class Contractor {
    
    public static final Contractor IMP = new Contractor();
    
    
    
    private static final UUID CONSOLE = UUID.randomUUID();
    
    private KeyPool<String> pool;
    private BlockSessionFactory sessionFactory;
    
    private Contractor() {
        ExecutorService executor = StructureCraft.IMP.getExecutor();
        this.pool = new KeyPool<>(executor);
    }
    
    
    
   
    
    public void build(final Structure structure, @Nullable Player player) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private List<Structure> traverse(Structure structure, boolean reversed) {
        GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        List<Structure> structures = Lists.newArrayList();
        try (Transaction tx = graph.beginTx()) {
            StructureRepository sr = new StructureRepository(graph);
            StructureNode sn = sr.findById(structure.getId());
            
            TraversalDescription td = graph.traversalDescription()
                    .breadthFirst()
                    .relationships(RelTypes.SUBSTRUCTURE_OF, Direction.INCOMING);
            
                    
            if (reversed) {
                td.reverse();
            }
            
            td.traverse(sn.getNode()).nodes().forEach(node -> {
                if (node.hasLabel(StructureNode.label())) {
                    structures.add(new Structure(node));
                }
            });
            
            tx.success();
        }
        return structures;
    }
    
    public void demolish(Structure structure) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public void rollback(Structure structure) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public void cancel(Structure structure) {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        Preconditions.checkNotNull(structure.getWorldName(), "Structure's world may not be null");
        
    }
    

}
