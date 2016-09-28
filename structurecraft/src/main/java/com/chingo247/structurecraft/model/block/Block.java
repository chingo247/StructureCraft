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
package com.chingo247.structurecraft.model.block;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 * Represents a block that is part of a container.
 * @author Chingo
 */
public class Block extends BaseBlock {

    private int x, y, z;
    
    public Block(int x, int y, int z, int id, int data) {
        this(x, y, z, id, data, null);
    }

    public Block(int x, int y, int z, int id, int data, CompoundTag ct) {
        super(id, data, ct);
        
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
