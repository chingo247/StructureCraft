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
package com.chingo247.structureapi.construction.fawe;

import com.boydti.fawe.object.FaweChunk;
import com.boydti.fawe.object.FaweQueue;
import com.boydti.fawe.object.RunnableVal2;
import com.chingo247.structureapi.construction.block.BlockSession;
import com.chingo247.structureapi.io.blockstore.safe.SafeBlockStore;
import com.chingo247.structureapi.util.ErrorHandler;
import com.google.common.collect.Lists;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;

/**
 *
 * @author Chingo
 */
public class FaweBlockSession extends BlockSession {
    
    private static final ErrorHandler ERROR_HANDLER = ErrorHandler.getHandler(FaweBlockSession.class);
    
    private List<Runnable> onCancel;
    private FaweQueue queue;
    private EditSession session;
    private Player player;

    public FaweBlockSession(FaweQueue queue, EditSession session, @Nullable Player player) {
        this.queue = queue;
        this.session = session;
        this.queue.addEditSession(session);
        this.player = player;
        this.onCancel = Lists.newArrayList();
    }
    
    public void setRollback(final SafeBlockStore blockStore, final CuboidRegion region) {
        final int startY = region.getMinimumY();
        final int endY = region.getMaximumY();
        
        this.queue.setChangeTask(new RunnableVal2<FaweChunk, FaweChunk>() {

            @Override
            public void run(FaweChunk before, FaweChunk u) {
                int startX = before.getX();
                int startZ = before.getZ();
                int endX = startX + 16;
                int endZ = startZ + 16;
                for (int y = startY; y < endY; y++) {
                    for (int x = startX; x < endX; x++) {
                        for (int z = startZ; z < endZ; x++) {
                            int data = queue.getCombinedId4Data(x, y, z);
                            int blockId = before.getBlockCombinedId(x - before.getX(), z, z - before.getZ());
                            CompoundTag tag = before.getTile(x - before.getX(), y, z - before.getX());
                            blockStore.setBlockAt(x - startX, y - startY, z - startZ, blockId, data, tag);
                        }
                    }
                }
                try {
                    blockStore.save();
                } catch (IOException ex) {
                    queue.cancel();
                    ERROR_HANDLER.handle(ex, "An error occured during backup operation, aborting place operation", player);
                }
            }
        });
        
    }
    
    @Override
    public void setBlock(int x, int y, int z, BaseBlock block) {
        BaseBlock filteredBlock = filterBlock(x, y, z, block);
        if(filteredBlock != null) {
            queue.setBlock(x, y, z, (short)block.getId(), (byte) block.getData());
            if(filteredBlock.getNbtData() != null) {
                queue.setTile(x, y, z, filteredBlock.getNbtData());
            }
        }
    }
    
    public void onComplete(Runnable runnable) {
        queue.addNotifyTask(runnable);
    }
    
    public void onCancel(Runnable runnable) {
        this.onCancel.add(runnable);
    }
    
    @Override
    public void cancel() {
        onCancel.forEach(runnable -> { runnable.run(); });
        queue.cancel();
    }
    
    @Override
    public void commit() {
        queue.enqueue();
    }
    
}
