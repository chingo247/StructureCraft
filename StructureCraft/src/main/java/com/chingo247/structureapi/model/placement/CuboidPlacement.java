package com.chingo247.structureapi.model.placement;

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

import com.chingo247.structureapi.construction.block.BlockSession;
import com.chingo247.structureapi.model.block.Block;
import com.chingo247.structureapi.model.IRotational;
import com.chingo247.structureapi.util.WorldUtil;
import com.chingo247.structureapi.util.iterator.CuboidIterator;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Iterator;

/**
 *
 * @author Chingo
 */
public abstract class CuboidPlacement extends AbstractPlacement<CuboidRegion> implements IPlacement, IRotational {
    
    public CuboidPlacement(int width, int height, int length) {
        this(null, width, height, length);
    }

    public CuboidPlacement(Vector relativePosition, int width, int height, int length) {
        super(relativePosition, new CuboidRegion(Vector.ZERO, new BlockVector(width, height, length)));
    }

    public Iterator<Vector> getIterator() {
        return new CuboidIterator(
                16,
                16,
                16
        ).iterate(getSize());
    }
    
    @Override
    public void place(BlockSession session, Vector pos) {
        Iterator<Vector> traversal = getIterator();
        // Cube traverse this clipboard
        while (traversal.hasNext()) {
            Vector v = traversal.next();
            BaseBlock clipboardBlock = getBlock(v.getBlockX(), v.getBlockY(), v.getBlockZ());

            if (clipboardBlock == null) {
                continue;
            }

            Block block = new Block(position.getBlockX(), position.getBlockY(), position.getBlockZ(), clipboardBlock.getId(), clipboardBlock.getData());
            
            block = transformBlock(block);
            Vector placingPosition = transformPosition(block, pos, v);
            
            session.setBlock(placingPosition.getBlockX(), placingPosition.getBlockY(), placingPosition.getBlockZ(), block);
        }
    }
    
    protected Block transformBlock(Block block) {
        Vector p;
        switch (WorldUtil.getDirection(getRotation())) {
            case EAST:
                break;
            case WEST:
                block.rotate90();
                block.rotate90();
                break;
            case NORTH:
                block.rotate90Reverse();
                break;
            case SOUTH:
                block.rotate90();
                break;
            default:
                throw new AssertionError("unreachable");
        }
        return block;
    }
    
    protected Vector transformPosition(Block block, Vector position, Vector blockPosition) {
        Vector p;
        switch (WorldUtil.getDirection(getRotation())) {
            case EAST:
                p = position.add(blockPosition);
                break;
            case WEST:
                p = position.add((-blockPosition.getBlockX()) + (getWidth() - 1), blockPosition.getBlockY(), (-blockPosition.getBlockZ()) + (getLength() - 1));
                break;
            case NORTH:
                p = position.add(blockPosition.getBlockZ(), blockPosition.getBlockY(), (-blockPosition.getBlockX()) + (getWidth() - 1));
                break;
            case SOUTH:
                p = position.add((-blockPosition.getBlockZ()) + (getLength() - 1), blockPosition.getBlockY(), blockPosition.getBlockX());
                break;
            default:
                throw new AssertionError("unreachable");
        }
        return p;
    }

  

    protected abstract BaseBlock getBlock(int blockX, int blockY, int blockZ);
    
}
