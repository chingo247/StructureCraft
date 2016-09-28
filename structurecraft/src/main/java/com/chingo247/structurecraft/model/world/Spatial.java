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
package com.chingo247.structurecraft.model.world;

import com.chingo247.structurecraft.util.WorldUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 *
 * @author Chingo247
 */
public class Spatial {

    private Long id;
    private int orientation;
    private int originX, originY, originZ;
    private int minX, maxX, minY, maxY, minZ, maxZ;
    private UUID worldUUID;
    private String structureType, world;

    /**
     * Constructor
     *
     * @param id The id of the spatial (or null)
     * @param minX The minX
     * @param maxX The maxX
     * @param minY The mixY
     * @param maxY The maxY
     * @param minZ The minZ
     * @param maxZ The maxZ
     *      * @param orientation The orientation
     * @param worldUUID The worldUUID
     */
    public Spatial(Long id, int minX, int maxX, int minY, int maxY, int minZ, int maxZ, UUID worldUUID, String world, int orientation) {
        this.id = id;
        this.orientation = orientation;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.worldUUID = worldUUID;
        this.world = world;
    }

    /**
     * Constructor
     *
     * @param orientation The orientation
     * @param minX The minX
     * @param maxX The maxX
     * @param minY The mixY
     * @param maxY The maxY
     * @param minZ The minZ
     * @param maxZ The maxZ
     * @param worldUUID The worldUUID
     */
    public Spatial(int minX, int maxX, int minY, int maxY, int minZ, int maxZ, UUID worldUUID, String world, int orientation) {
        this(null, minX, maxX, minY, maxY, minZ, maxZ, worldUUID, world, orientation);
    }

    /**
     * Gets the name of the world
     * @return The name of the world
     */
    public String getWorld() {
        return world;
    }
    
    /**
     * Gets the spatial id.<br/>
     * <b>NOTE:</b> May be null if not inserted in database
     *
     * @return The spatial id
     */
    public @Nullable
    Long getId() {
        return id;
    }

    public String getStructureType() {
        return structureType;
    }

    /**
     * Gets the orientation of this spatial.
     *
     * @return The orientation
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Gets the direction by converting the orientation to a cardinal directions
     * (NORTH, EAST, SOUTH, WEST).
     *
     * @return The cardinal direction
     */
    public Direction getDirection() {
        return Direction.direction(orientation);
    }

    /**
     * The origin x of this point. Used to get the relative position.
     *
     * @return The origin x //TODO add examples of placement defined in plan
     */
    public int getOriginX() {
        return originX;
    }

    /**
     * The origin x of this point. Used to get the relative position.
     *
     * @return The origin y //TODO add examples of placement defined in plan
     */
    public int getOriginY() {
        return originY;
    }

    /**
     * The origin x of this point. Used to get the relative position.
     *
     * @return The origin z //TODO add examples of placement defined in plan
     */
    public int getOriginZ() {
        return originZ;
    }

    /**
     * Gets the center x.
     *
     * @return The center x
     */
    public int getCenterX() {
        return maxX - ((maxX - minX) / 2);
    }

    /**
     * Gets the center y.
     *
     * @return The center y
     */
    public int getCenterY() {
        return maxY - ((maxY - minY) / 2);
    }

    /**
     * Gets the center z.
     *
     * @return The center z
     */
    public int getCenterZ() {
        return maxZ - ((maxZ - minZ) / 2);
    }

    /**
     * Gets the region coordinat based on the origin:
     * <br/><br/>
     * chunkX = x >> 4<br/>
     * chunkZ = z >> z<br/>
     * <br/>
     * RegionX = chunkX >> 5 <br/>
     * RegionZ = chunkZ >> 5 <br/>
     *
     * @return The region coordinate
     */
    public Vector2D getRegion() {
        int chunkX = getCenterX() >> 4;
        int chunkZ = getCenterZ() >> 4;
        return new BlockVector2D(chunkX >> 5, chunkZ >> 5);
    }

    /**
     * Gets the center of this spatial.
     *
     * @return The center
     */
    public Vector getCenter() {
        return new BlockVector(getCenterX(), getCenterY(), getCenterZ());
    }

    /**
     * Gets the origin.
     *
     * @return The origin
     */
    public Vector getOrigin() {
        return new BlockVector(originX, originY, originZ);
    }

    /**
     * Gets the min position.
     *
     * @return The min position
     */
    public Vector getMin() {
        return new BlockVector(minX, minY, minZ);
    }

    /**
     * Gets the min position.
     *
     * @return The min position
     */
    public Vector getMax() {
        return new BlockVector(maxX, maxY, maxZ);
    }

    /**
     * Gets the minX.
     *
     * @return The min X
     */
    public int getMinX() {
        return minX;
    }

    /**
     * Gets the max X
     *
     * @return The max X
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * Gets the min Y
     *
     * @return The min Y
     */
    public int getMinY() {
        return minY;
    }

    /**
     * Gets the max Y
     *
     * @return The max Y
     */
    public int getMaxY() {
        return maxY;
    }

    /**
     * Gets the min Z
     *
     * @return The min Z
     */
    public int getMinZ() {
        return minZ;
    }

    /**
     * Gets the max Z
     *
     * @return the max Z
     */
    public int getMaxZ() {
        return maxZ;
    }

    /**
     * Gets the worldUUID
     *
     * @return The worldUUID
     */
    public UUID getWorldUUID() {
        return worldUUID;
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
     *
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

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setOriginX(int originX) {
        this.originX = originX;
    }

    public void setOriginY(int originY) {
        this.originY = originY;
    }

    public void setOriginZ(int originZ) {
        this.originZ = originZ;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    public void setWorldUUID(UUID worldUUID) {
        this.worldUUID = worldUUID;
    }

    public void setType(String structureType) {
        this.structureType = structureType;
    }

}
