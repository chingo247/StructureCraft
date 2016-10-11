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
package com.chingo247.structurecraft.model.schematic;

/**
 *
 * @author Chingo
 */
public class SchematicCache {
    
    private int width, height, length, blocks;
    private long hash;

    public SchematicCache(int width, int height, int length, int blocks, long hash) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = blocks;
        this.hash = hash;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public int getBlocks() {
        return blocks;
    }

    public long getHash() {
        return hash;
    }
    
}
