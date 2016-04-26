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
package com.chingo247.structureapi.session;

import com.chingo247.settlercraft.core.model.world.World;
import com.chingo247.structureapi.placement.BlockFilter;
import com.chingo247.structureapi.placement.BlockPredicate;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.entity.Player;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chingo
 */
public abstract class BlockPlaceSession {
    
    private final World world;
    private final Player player;
    private final List<BlockPredicate> toIgnore;
    private final List<BlockFilter> masks;

    public BlockPlaceSession(World world, Player player) {
        this.world = world;
        this.player = player;
        this.toIgnore = new ArrayList<>();
        this.masks = new ArrayList<>();
    }

    /**
     * Adds a block predicate, when the block predicate returns true, the block will not be placed
     * during the place of the placement
     * @param blockPredicate The blockPredicate
     */
    public void addIgnore(BlockPredicate blockPredicate) {
        toIgnore.add(blockPredicate);
    }
    
     public void addBlockMask(BlockFilter mask) {
        this.masks.add(mask);
    }
    
    /**
     * Returns all the BlockPredicates that will be determine if a block should be ignored
     * @return The blockPredicates
     */
    public Iterable<BlockPredicate> getIgnore() {
        return toIgnore;
    }
    
    public Iterable<BlockFilter> getBlockMasks() {
        return masks;
    } 
    
    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }
    
    public abstract void setBlock(int x, int y, int z, BaseBlock block);
    
    public void setBlock(Vector position, BaseBlock block) {
        setBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ(), block);
    }
    
}
