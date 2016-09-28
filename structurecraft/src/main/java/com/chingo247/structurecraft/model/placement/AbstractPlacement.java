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
package com.chingo247.structurecraft.model.placement;

import com.chingo247.structurecraft.model.IRotational;
import com.chingo247.structurecraft.model.container.BlockContainer;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;

/**
 * 
 * @author Chingo
 * @param <T>
 */
public abstract class AbstractPlacement<T extends Region> extends BlockContainer<T> implements IPlacement, IRotational {
    
    protected final Vector position;
    protected int rotation = 0;
    
    public AbstractPlacement(T region) {
        this(Vector.ZERO, region);
    }
    
    public AbstractPlacement(Vector relativePosition, T region) {
        super(region);
        this.position = relativePosition;
    }

    
    
}
