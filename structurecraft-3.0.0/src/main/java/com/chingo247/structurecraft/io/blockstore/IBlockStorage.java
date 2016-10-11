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
package com.chingo247.structurecraft.io.blockstore;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 *
 * @author Chingo
 */
public interface IBlockStorage{
    
    BaseBlock getBlockAt(Vector position);
    
    BaseBlock getBlockAt(int x, int y, int z);
    
    void setBlockAt(int x, int y, int z, BaseBlock b);
    
    void setBlockAt(Vector position, BaseBlock b);
    
    void setBlockAt(int x, int y, int z, int blockId, int blockData, CompoundTag tag);
    
    
}
