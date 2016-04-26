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
package com.chingo247.structureapi.placement;

import com.chingo247.structureapi.placement.io.AbstractPlacement;
import com.chingo247.structureapi.session.BlockPlaceSession;
import com.chingo247.structureapi.util.WorldUtil;
import com.chingo247.structureapi.util.iterator.CuboidIterator;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 *
 * @author Chingo
 */
public abstract class BlockPlacement extends AbstractPlacement implements IPlacement, IRotational, IBlockContainer {

    protected static final int PRIORITY_REDSTONE = 5;
    protected static final int PRIORITY_FIRST = 4;
    protected static final int PRIORITY_LIQUID = 3;
    protected static final int PRIORITY_LATER = 2;
    protected static final int PRIORITY_FINAL = 1;

    protected final int BLOCK_BETWEEN;
    protected final int MAX_PLACE_LATER_TO_PLACE = 10;
    
    public BlockPlacement(int width, int height, int length) {
        super(width, height, length);
        this.BLOCK_BETWEEN = Math.round((float) ((numBlocks() * 0.001)));
    }

    public BlockPlacement(Vector relativePosition, int width, int height, int length) {
        super(relativePosition, width, height, length);
        this.BLOCK_BETWEEN = Math.round((float) ((numBlocks() * 0.01)));
    }

    public final int numBlocks() {
        return width * height * length;
    }
    
    public Iterator<Vector> getIterator() {
        return new CuboidIterator(
                16,
                16,
                16
        ).iterate(getSize());
    }
    
    @Override
    public void place(BlockPlaceSession session, Vector pos) {
        Iterator<Vector> traversal = getIterator();
        PriorityQueue<Block> placeLater = new PriorityQueue<>();

        
        
        int placeLaterPlaced = 0;
        int placeLaterPause = 0;

        // Cube traverse this clipboard
        while (traversal.hasNext()) {
            Vector v = traversal.next();
            BaseBlock clipboardBlock = getBlock(v);

            if (clipboardBlock == null) {
                continue;
            }

            int priority = getPriority(clipboardBlock);

            Block b = new Block(position, clipboardBlock.getId(), clipboardBlock.getData());
            
            if (priority == PRIORITY_FIRST) {
                doBlock(session, pos, v, b);
            } else {
                placeLater.add(b);
            }

            // For every X place intensive blocks
            if (placeLaterPause > 0 && clipboardBlock.getId() != 0) {
                placeLaterPause--;
            } else {

                // only place these when having a greater xz-cubevalue to avoid placing torches etc in air and break them later
                while (placeLater.peek() != null
                        && placeLater.peek().getPosition().getBlockY() < v.getBlockY()
                        && (placeLater.peek().getPosition().getBlockX() % 16) > (v.getBlockX() % 16)
                        && (placeLater.peek().getPosition().getBlockZ() % 16) > (v.getBlockZ() % 16)) {
                    Block plb = placeLater.poll();
                    doBlock(session, pos, plb.getPosition(), plb);

                    placeLaterPlaced++;

                    if (getPriority(clipboardBlock) == PRIORITY_LIQUID || BlockType.emitsLight(plb.getId())) {
                        placeLaterPlaced++;
                    }

                    if (placeLaterPlaced >= MAX_PLACE_LATER_TO_PLACE) {
                        placeLaterPause = BLOCK_BETWEEN;
                        placeLaterPlaced = 0;
                    }
                }
            }

        }
        // Empty the queue
        while (placeLater.peek() != null) {
            Block plb = placeLater.poll();
            doBlock(session, pos, plb.getPosition(), plb);
        }

    }

    protected int getPriority(BaseBlock block) {
        if (isWater(block) || isLava(block)) {
            return PRIORITY_LIQUID;
        }

        if (BlockType.shouldPlaceLast(block.getId()) || BlockType.emitsLight(block.getId())) {
            return PRIORITY_LATER;
        }

        if (BlockType.shouldPlaceFinal(block.getId())) {
            return PRIORITY_FINAL;
        }

        return PRIORITY_FIRST;

    }

    protected boolean isLava(BaseBlock b) {
        int bi = b.getType();
        if (bi == BlockID.LAVA || bi == BlockID.STATIONARY_LAVA) {
            return true;
        }
        return false;
    }

    protected boolean isWater(BaseBlock b) {
        int bi = b.getType();
        if (bi == BlockID.WATER || bi == BlockID.STATIONARY_WATER) {
            return true;
        }
        return false;
    }

    @Override
    public abstract BaseBlock getBlock(Vector position);

    public BaseBlock getBlock(int x, int y, int z) {
        return getBlock(new BlockVector(x, y, z));
    }

    protected void doBlock(BlockPlaceSession editSession, Vector position, Vector blockPosition, Block block) {
        Vector p;

        switch (WorldUtil.getDirection(getRotation())) {
            case EAST:
                p = position.add(blockPosition);
                break;
            case WEST:
                p = position.add((-blockPosition.getBlockX()) + (getWidth() - 1), blockPosition.getBlockY(), (-blockPosition.getBlockZ()) + (getLength() - 1));
                block.rotate90();
                block.rotate90();
                break;
            case NORTH:
                p = position.add(blockPosition.getBlockZ(), blockPosition.getBlockY(), (-blockPosition.getBlockX()) + (getWidth() - 1));
                block.rotate90Reverse();
                break;
            case SOUTH:
                p = position.add((-blockPosition.getBlockZ()) + (getLength() - 1), blockPosition.getBlockY(), blockPosition.getBlockX());
                block.rotate90();
                break;
            default:
                throw new AssertionError("unreachable");
        }

        for (BlockPredicate bp : editSession.getIgnore()) {
            if (bp.evaluate(blockPosition, p, block)) {
                return;
            }
        }

        for (BlockFilter bm : editSession.getBlockMasks()) {
            bm.apply(blockPosition, p, block);
        }

        editSession.setBlock(p, block);
    }
    
}
