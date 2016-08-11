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
package com.chingo247.structureapi.model.container;

import com.chingo247.structureapi.util.RegionUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

/**
 *
 * @author Chingo
 */
public abstract class BlockContainer<T extends Region> implements IBlockContainer<T> {

    private T region;
    private Vector offset;
    private int rotation;

    public BlockContainer(Vector offset, T region) {
        this.region = region;
        this.offset = offset == null ? Vector.ZERO : offset;
    }

    public BlockContainer(T region) {
        this(null, region);
    }

    @Override
    public T getRegion() {
        return region;
    }

    public CuboidRegion getCuboidRegion() {
        return CuboidRegion.makeCuboid(getRegion());
    }
    
    public Vector getOffset() {
        return offset;
    }
    
    public final void rotate(int rotation) {
        float ya = this.rotation + rotation;
        if(ya > 360) {
            int times = (int)((ya - (ya % 360)) / 360);
            int normalizer = times * 360;
            ya -= normalizer;
        } else if (ya < -360) {
            ya = Math.abs(ya);
            int times = (int)((ya - (ya % 360)) / 360);
            int normalizer = times * 360;
            ya = ya + normalizer;
        }
        this.rotation = (int) (ya);
    }

    public Vector getSize() {
        return RegionUtil.getSize(CuboidRegion.makeCuboid(region));
    }
    
    public int getRotation() {
        return rotation;
    }

    public int getHeight() {
        return region.getHeight();
    }

    public int getLength() {
        return region.getLength();
    }

    public int getWidth() {
        return region.getWidth();
    }

}
