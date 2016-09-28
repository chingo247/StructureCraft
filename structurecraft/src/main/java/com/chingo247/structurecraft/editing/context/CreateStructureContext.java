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
package com.chingo247.structurecraft.editing.context;

import com.chingo247.structurecraft.model.placement.IPlacement;
import com.chingo247.structurecraft.model.plans.StructurePlan;
import com.chingo247.structurecraft.model.structure.AccessType;
import com.chingo247.structurecraft.model.world.Direction;
import com.chingo247.structurecraft.util.functions.FileResource;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class CreateStructureContext extends EditContext {
    
    private final Map<UUID,AccessType> owners;
    private StructurePlan plan;
    private String name;
    private List<FileResource> resources;
    private double refundValue;
    private final Vector position;
    private final Direction direction;
    private final IPlacement placement;

    /**
     * Constructor.
     * @param world
     * @param creator
     * @param structureName
     * @param position
     * @param direction
     * @param placement
     */
    public CreateStructureContext(World world, Player creator, String structureName, Vector position, Direction direction, IPlacement placement) {
        super(world, creator);
        Preconditions.checkNotNull(structureName, "Name of the structure may not be null!");
        Preconditions.checkArgument(structureName.trim().isEmpty(), "Name of the structure may not be empty!");
        Preconditions.checkNotNull(position, "Position may not be null");
        Preconditions.checkNotNull(direction, "Direction may not be null");
        
        this.name = structureName;
        this.owners = new HashMap<>();
        this.position = position;
        this.direction = direction;
        this.placement = placement;
    }

    public IPlacement getPlacement() {
        return placement;
    }
    
    public void addFile(File file) {
        addResource((f) -> { try {
            Files.copy(file, f);
            } catch (IOException ex) {
                Logger.getLogger(CreateStructureContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public void addFiles(File... files) {
        for (File f : files) {
            addFile(f);
        }
    }
    
    public void addResource(FileResource resource) {
        if(this.resources == null) resources = new ArrayList<>();
        
        this.resources.add(resource);
    }
    
    public void addResources(FileResource... resources) {
        this.resources.addAll(Arrays.asList(resources));
    }
    
    public Vector getPosition() {
        return position;
    }
    
    public Direction getDirection() {
        return direction;
    }
    
    public String getName() {
        return name;
    }

    public double getRefundValue() {
        return refundValue;
    }
    
    public void addOwner(UUID player, AccessType accessType) {
        this.owners.put(player, accessType);
    }
    
    public void addOwners(Iterable<UUID> players, AccessType accessType) {
        players.forEach((player) -> addOwner(player, accessType));
    }
    
    public void setRefundValue(double price) {
        this.refundValue = price;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPlan(StructurePlan plan) {
        this.plan = plan;
    }
    
    public Map<UUID,AccessType> getOwners() {
        return Maps.newHashMap(owners);
    }

    public StructurePlan getPlan() {
        return plan;
    }
    
}
