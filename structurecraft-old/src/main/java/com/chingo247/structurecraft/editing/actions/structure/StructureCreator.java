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
package com.chingo247.structurecraft.editing.actions.structure;

import com.chingo247.structurecraft.StructureCraft;
import com.chingo247.structurecraft.StructureCraftAPI;
import com.chingo247.structurecraft.edting.ActionSubmitter;
import com.chingo247.structurecraft.edting.PlayerActionSubmitter;
import com.chingo247.structurecraft.editing.Scheduler;
import com.chingo247.structurecraft.editing.context.PlaceContext;
import com.chingo247.structurecraft.editing.restrictions.Restriction;
import com.chingo247.structurecraft.editing.restrictions.Violation;
import com.chingo247.structurecraft.events.structure.StructureCreateEvent;
import com.chingo247.structurecraft.exception.placement.PlacementException;
import com.chingo247.structurecraft.io.placement.PlacementRegister;
import com.chingo247.structurecraft.model.OwnerType;
import com.chingo247.structurecraft.model.placement.IPlacement;
import com.chingo247.structurecraft.model.plans.StructurePlan;
import com.chingo247.structurecraft.model.structure.AccessType;
import com.chingo247.structurecraft.model.structure.ConstructionStatus;
import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.model.world.Direction;
import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.model.world.SpatialTypes;
import com.chingo247.structurecraft.persistence.repositories.StructureRepository;
import com.chingo247.structurecraft.services.ServicesManager;
import com.chingo247.structurecraft.events.event.EventDispatcher;
import com.chingo247.structurecraft.util.PlacementUtil;
import com.chingo247.structurecraft.util.functions.FileResource;
import com.chingo247.structurecraft.util.value.Holder;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author chingo247
 */
public class StructureCreator {

    private final Map<UUID, OwnerType> owners;
    private List<FileResource> resources;
    private IPlacement placement;
    private StructurePlan plan;
    private double refundValue;

    public StructureCreator() {
        this.refundValue = 0.0;
        this.owners = Maps.newHashMap();
    }
    
    public StructureCreator setRefundValue(double refundValue) {
        this.refundValue = refundValue;
        return this;
    }
    
    public StructureCreator setPlacement(IPlacement placement) {
        this.placement = placement;
        return this;
    }

    public StructureCreator setPlan(StructurePlan plan) {
        this.plan = plan;
        return this;
    }
    
    public StructureCreator addOwner(UUID player, OwnerType ownerType) {
        this.owners.put(player, ownerType);
        return this;
    }

    public StructureCreator addOwners(Iterable<UUID> players, OwnerType ownerType) {
        players.forEach((player) -> addOwner(player, ownerType));
        return this;
    }

    public StructureCreator addFile(File file) {
        addResource((f) -> {
            try {
                Files.copy(file, new File(f, file.getName()));
            } catch (IOException ex) {
                Logger.getLogger(StructureCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return this;
    }

    public StructureCreator addFiles(File... files) {
        for (File f : files) {
            addFile(f);
        }
        return this;
    }

    public StructureCreator addResource(FileResource resource) {
        if (this.resources == null) {
            resources = new ArrayList<>();
        }

        this.resources.add(resource);
        return this;
    }

    public StructureCreator addResources(FileResource... resources) {
        this.resources.addAll(Arrays.asList(resources));
        return this;
    }
    
    public Structure create(Player player, String name, String world, Vector pos, Direction direction) throws PlacementException {
        return create(new PlayerActionSubmitter(player), name, world, pos, direction);
    }
    
    public Structure create(ActionSubmitter submitter, String name, String world, Vector pos, Direction direction) throws PlacementException {
        if (plan == null && placement == null) throw new RuntimeException("No plan or placement was set!");
        
        IWorld w = StructureCraft.IMP.getPlatform().getServer().getWorld(world);
        if (w == null) {
            throw new IllegalArgumentException("World with name '" + world + "' was not found!");
        }
        World weWorld = ServicesManager.IMP.getWorldedit().getWorld(world);
        IPlacement p = this.placement != null ? this.placement : plan.getPlacementSource().load();
        
        final Vector point2 = PlacementUtil.getPoint2Right(pos, direction, pos.add(p.getCuboidRegion().getMaximumPoint()));
        final CuboidRegion affected = new CuboidRegion(pos, point2);
        final Holder<Structure> h = new Holder<>();

        Scheduler.IMP.executeCritical(w.getName(), () -> { // Waits for lock
            PlaceContext placeContext = new PlaceContext(weWorld, (submitter instanceof PlayerActionSubmitter ? ((PlayerActionSubmitter) submitter).getPlayer() : null), affected);

            for (Restriction<PlaceContext> restriction : StructureCraft.IMP.getRestrictions()) {
                Violation violation = restriction.checkViolation(placeContext);
                if (violation != null) {
                    submitter.sendError(violation.getMessage());
                    return;
                }
            }

            DBI dbi = ServicesManager.IMP.getDBI();
            try (Handle handle = dbi.open()) {
                Handle tx = handle.begin();
                try {
                    StructureRepository sr = StructureCraftAPI.newStructureRepository(handle);
                    Vector min = affected.getMinimumPoint(), max = affected.getMaximumPoint();
                    Spatial spatial = new Spatial(w.getUUID(), w.getName(), new CuboidRegion(min, max), direction, SpatialTypes.STRUCTURE);
                    Structure structure = new Structure(spatial, name);
                    structure.setRefundValue(refundValue);
                    structure.setStatus(ConstructionStatus.ON_HOLD);
                    long id = sr.add(structure);
                    structure.setId(id);
                    h.setValue(structure);
                    
                    File sdir = structure.getDirectory();
                    resources.forEach((resource) -> {
                        resource.writeTo(sdir);
                    });
                    
                    if (plan != null) {
                        Files.copy(plan.getFile(), new File(sdir, "structureplan.xml"));
                    }
                    
                    
                    
                    tx.commit();
                } catch (Exception ex) {
                    tx.rollback();
                } finally {
                    tx.close();
                }
            }

        });
        
        if (h.getValue() != null) {
            EventDispatcher.IMP.dispatchEvent(new StructureCreateEvent((submitter instanceof PlayerActionSubmitter) ? ((PlayerActionSubmitter) submitter).getPlayer() : null, h.getValue()));
        }
        
        return h.getValue();
        
    }
    
    
}
