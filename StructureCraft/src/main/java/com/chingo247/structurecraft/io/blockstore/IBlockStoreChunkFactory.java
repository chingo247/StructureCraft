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

import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.Vector2D;

/**
 *
 * @author Chingo
 */
public interface IBlockStoreChunkFactory<T extends IBlockStoreChunk> {
    
    T newChunk(Tag sectionTag, int x, int z, Vector2D size);
    
}
