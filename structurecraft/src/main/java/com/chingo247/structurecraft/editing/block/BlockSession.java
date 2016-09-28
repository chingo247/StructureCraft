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
package com.chingo247.structurecraft.editing.block;

import com.chingo247.structurecraft.model.structure.Structure;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.entity.Player;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chingo
 */
public abstract class BlockSession {
    
    private List<BlockPredicate> ignore;
    private List<BlockMask> masks;
    private Vector origin;
    private Structure structure;
    protected List<Runnable> onCancel, onComplete;
    private Player player;

    public BlockSession(Player player) {
        this.ignore = new ArrayList<>();
        this.origin = Vector.ZERO;
        this.player = player;
    }
    
    public void setOrigin(Vector origin) {
        this.origin = origin;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }
    
    public abstract void setBlock(int x, int y, int z, BaseBlock block);
    
    protected BaseBlock filterBlock(int x, int y ,int z, BaseBlock block) {
        for(BlockPredicate predicate : ignore) {
            if(predicate.evaluate(x - origin.getBlockX(), y - origin.getBlockY(), z - origin.getBlockZ(), x, y, z, block)) {
                return null;
            }
        }
        
        for(BlockMask mask : masks) {
            if(mask.mask(x - origin.getBlockX(), y - origin.getBlockY(), z - origin.getBlockZ(), x, y, z, block)) {
                break;
            }
        }
        return block;
    }
    
    public void onComplete(Runnable runnable) {
        if(onComplete == null) onComplete = new ArrayList<>();
        onComplete.add(runnable);
    }
    
    public void onCancel(Runnable runnable) {
        if (onCancel == null) onCancel = new ArrayList<>();
        this.onCancel.add(runnable);
    }
    
    public void cancel() {
        if(onCancel != null) {
            onCancel.forEach(runnable -> { runnable.run(); });
        }
    }
}
