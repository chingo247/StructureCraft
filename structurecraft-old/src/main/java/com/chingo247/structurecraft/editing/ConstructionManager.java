/*
 * Copyright (C) 2016 ching
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
package com.chingo247.structurecraft.editing;

import com.chingo247.structurecraft.edting.ActionSubmitter;
import com.chingo247.structurecraft.editing.actions.StructureAction;
import com.chingo247.structurecraft.editing.actions.structure.Construction;
import com.chingo247.structurecraft.edting.block.BlockSession;
import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.persistence.dao.StructureDAO;
import com.chingo247.structurecraft.services.ServicesManager;
import com.chingo247.structurecraft.util.functions.ProduceWithInput;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author ching
 */
public class ConstructionManager {
    
    public static final ConstructionManager IMP = new ConstructionManager();
    
    private final Map<Long, LinkedConstruction> constructionTasks;
    
    private ConstructionManager() {
        this.constructionTasks = Maps.newHashMap();
    }
    
    public void execute(ActionSubmitter submitter, Structure structure, ProduceWithInput<Construction, Structure> actionProducer, boolean recursive, boolean traverseDown) {
        synchronized (constructionTasks) {
            cancelWithoutLock(structure);

            if (!recursive) {
                Construction action = actionProducer.create(structure);

                action.execute();
            } else {
                // Get the substructures
                List<Structure> structures = getTree(structure.getId(), traverseDown);

                // Execute this critical
                final String world = structure.getSpatial().getWorld();

                Scheduler.IMP.queue(world, () -> {
                    Construction first = null;
                    BlockSession prevSession = null;
                    for (Structure s : structures) {

                        // Get the session and cancel any active actions
                        Construction action = actionProducer.create(s);



                        // Chain tasks...
                        if (prevSession != null) {
                            prevSession.onComplete(() -> {
                                action.execute();
                            });
                        }

                        // Remember the first one, as this is where we want to start the action
                        if (first == null) {
                            first = action;
                        }

                        prevSession = action.getBlockSession();
                    }
                    if (first != null) {
                        first.execute();
                    }
                });

            }
        }
    }

    private List<Structure> getTree(long root, boolean traverseDown) {
        List<Structure> structures;
        try (Handle handle = ServicesManager.IMP.getDBI().open()) {
            StructureDAO sdao = handle.attach(StructureDAO.class);
            structures = sdao.getTree(root, traverseDown);
        }
        return structures;
    }
    
    public void cancel(Structure structure) {
        synchronized (constructionTasks) {
            cancelWithoutLock(structure);
        }
    }

    private void cancelWithoutLock(Structure structure) {
        LinkedConstruction task = constructionTasks.get(structure.getId());
        if (task != null) {
            task.cancel();
        }
    }
    
    private class LinkedConstruction implements StructureAction {
        
        private Construction task;
        private LinkedConstruction next;
        
        public LinkedConstruction(Construction task) {
            this.task = task;
        }

        @Override
        public Structure getStructure() {
            return task.getStructure();
        }
        
        @Override
        public void execute() {
            this.task.execute();
        }
        
        public void setNext(LinkedConstruction next) {
            this.next = next;
        }
        
        public void cancel() {
            this.task.cancel();
            constructionTasks.remove(task.getStructure().getId());
            if (next != null) {
                next.cancel();
            }
        }
        
    }
    
}
