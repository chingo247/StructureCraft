/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.structureapi.model.persistent.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.StructureCraft;
import com.chingo247.structureapi.model.persistent.plot.Plot;
import com.chingo247.structureapi.model.plans.StructurePlan;
import com.chingo247.structureapi.exception.plans.StructurePlanException;
import com.chingo247.structureapi.io.blockstore.BlockStore;
import com.chingo247.structureapi.io.blockstore.safe.SafeBlockStoreReader;
import com.chingo247.structureapi.io.plans.StructurePlanReader;
import com.chingo247.structureapi.util.WorldUtil;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.neo4j.graphdb.Node;

/**
 * As opposed to the {@link StructureNode} this unmodifable structure has all it's properties loaded. 
 * None of the opertions of this class have to be executed within a transaction
 * @author Chingo
 */
public class Structure extends Plot  {
    
    public static final String ROLLBACK_DIRECTORY = "rollback";
    
    private Long id;
    private String name;
    private Vector origin;
    private ConstructionStatus status;
    private double price;
    private Direction direction;
    
    private Date completedAt;
    private Date createdAt;
    private Date deletedAt;

    public Structure(Node structureNode) {
        this(new StructureNode(structureNode));
    }
    
    public Structure(StructureNode structure) {
        super(structure.getNode());
        this.id = structure.getId();
        this.name = structure.getName();
        this.origin = structure.getOrigin();
        this.status = structure.getStatus();
        this.price = structure.getPrice();
        this.direction = structure.getDirection();
        this.deletedAt = structure.getDeletedAt();
        this.createdAt = structure.getCreatedAt();
        this.completedAt = structure.getCompletedAt();
    }

  
    
    /**
     * Gets the id of the structure
     *
     * @return The id of the structure
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the structure
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the origin of this Structure
     *
     * @return The position
     */
    public Vector getOrigin() {
        return origin;
    }

    /**
     * Gets the value/price of this structure
     *
     * @return The value/price of this structure
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the direction in which this structure is oriented
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * The current construction status of this structure
     *
     * @return The construction status
     */
    public ConstructionStatus getConstructionStatus() {
        return status;
    }

    /**
     * Gets when this structure was completed, may return null
     *
     * @return The date of completion
     */
    public Date getCompletedAt() {
        return completedAt;
    }
    
    

    /**
     * Gets when this structure was created
     *
     * @return The date this structure was created
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the date when this structure was removed. may return null
     *
     * @return The date of removal
     */
    public Date getDeletedAt() {
        return deletedAt;
    }

    public ConstructionStatus getStatus() {
        return status;
    }
    
    
    
    /**
     * Will add the offset to the structure's origin, which is always the front
     * left corner of a structure.
     *
     * @param offset The offset
     * @return the location
     */
    public Vector translateRelativeLocation(Vector offset) {
        Vector p = WorldUtil.translateLocation(getOrigin(), getDirection(), offset.getX(), offset.getY(), offset.getZ());
        return new Vector(p.getBlockX(), p.getBlockY(), p.getBlockZ());
    }

    /**
     * Gets the relative position
     * @param worldPosition The worldposition
     * @return The relative position
     */
    public Vector getRelativePosition(Vector worldPosition) {
        switch (getDirection()) {
            case NORTH:
                return new Vector(
                        worldPosition.getBlockX() - this.getOrigin().getX(),
                        worldPosition.getBlockY() - this.getOrigin().getY(),
                        this.getOrigin().getZ() - worldPosition.getBlockZ()
                );
            case SOUTH:
                return new Vector(
                        this.getOrigin().getX() - worldPosition.getBlockX(),
                        worldPosition.getBlockY() - this.getOrigin().getY(),
                        worldPosition.getBlockZ() - this.getOrigin().getZ()
                );
            case EAST:
                return new Vector(
                        worldPosition.getBlockZ() - this.getOrigin().getZ(),
                        worldPosition.getBlockY() - this.getOrigin().getY(),
                        worldPosition.getBlockX() - this.getOrigin().getX()
                );
            case WEST:
                return new Vector(
                        this.getOrigin().getZ() - worldPosition.getBlockZ(),
                        worldPosition.getBlockY() - this.getOrigin().getY(),
                        this.getOrigin().getX() - worldPosition.getBlockX()
                );
            default:
                throw new AssertionError("Unreachable");
        }
    }

    public File getDirectory() {
        return StructureCraft.IMP.getDirectoryForStructure(this);
    }
    

    public StructurePlan getStructurePlan() throws StructurePlanException {
        File planFile = new File(getDirectory(), "structureplan.xml");
        if(!planFile.exists()) {
            throw new StructurePlanException("Structure #" + getId() + " doesn't have a plan!");
        }

        StructurePlanReader loader = new StructurePlanReader();
        StructurePlan plan = loader.loadFile(planFile);

        return plan;
    }

    public BlockStore getBlockStore() throws IOException {
        SafeBlockStoreReader reader = new SafeBlockStoreReader();
        return reader.read(getBlockStoreDirectory());
    }
 
    private File getBlockStoreDirectory() {
        File rollbackDirectory = new File(getDirectory(), ROLLBACK_DIRECTORY);
        rollbackDirectory.mkdirs();
        return rollbackDirectory;
    }
    
    public boolean hasBlockStore() {
        return new File(getDirectory(), ROLLBACK_DIRECTORY).exists();
    }

    

}
