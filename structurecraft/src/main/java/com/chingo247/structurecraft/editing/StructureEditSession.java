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
package com.chingo247.structurecraft.editing;

import com.chingo247.structurecraft.editing.block.BlockSession;
import com.chingo247.structurecraft.exception.placement.PlacementException;
import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.persistence.dao.StructureDAO;
import com.chingo247.structurecraft.editing.actions.structure.Build;
import com.chingo247.structurecraft.editing.actions.structure.Construction;
import com.chingo247.structurecraft.editing.actions.structure.Demolish;
import com.chingo247.structurecraft.editing.actions.structure.Rollback;
import com.chingo247.structurecraft.editing.context.BlockPlaceContext;
import com.chingo247.structurecraft.services.ServicesManager;
import com.chingo247.structurecraft.util.functions.ProduceWithInput;
import com.sk89q.worldedit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author Chingo247
 */
public class StructureEditSession {

    private static final Logger LOG = Logger.getLogger(StructureEditSession.class.getName());

    private static final UUID CONSOLE = UUID.randomUUID();
    private final Structure structure;
    private Construction currentAction;

    public StructureEditSession(Structure structure) {
        this.structure = structure;
    }

    private void execute(Player player, Structure structure, ProduceWithInput<Construction, Structure> actionProducer, boolean recursive, boolean traverseDown) {
        cancel(new PlayerActionSubmitter(player));

        if (!recursive) {
            Construction action = actionProducer.create(structure);
            this.currentAction = action;
            action.execute();
        } else {
            // Get the substructures
            List<Structure> structures = getTree(structure.getId(), traverseDown);

            // Execute this critical
            final String world = structure.getSpatial().getWorld();
            final UUID playerUUID = player != null ? player.getUniqueId() : CONSOLE;
            Scheduler.IMP.queue(world, () -> {
                Construction first = null;
                BlockSession prevSession = null;
                for (Structure s : structures) {
                    // Get the session and cancel any active actions
                    
                    
                    Construction action = actionProducer.create(s);
                    StructureEditSession se = StructureEditSessionManager.IMP.getSession(s);
                    
                    
                    se.currentAction = action;

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

                    prevSession = action.getSession();
                }
                if (first != null) {
                    first.execute();
                }
            });

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

    public void build(BlockPlaceContext context, Player player, boolean recursive) throws PlacementException {
        execute(player, structure, (s) -> {
            return new Build(s, context);
        }, recursive, true);
    }

    public void demolish(BlockPlaceContext context, Player player, boolean recursive) {
        execute(player, structure, (s) -> {
            return new Demolish(s, context);
        }, recursive, false);
    }

    public void rollback(BlockPlaceContext context, Player player, boolean recursive) {
        execute(player, structure, (s) -> {
            return new Rollback(s, context);
        }, recursive, false);
    }

    public void restore(ActionSubmitter submitter) {

    }

    public void cancel(ActionSubmitter submitter) {
        if (currentAction != null) {
            currentAction.cancel();
        }
    }

    public boolean supportsHolograms() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
