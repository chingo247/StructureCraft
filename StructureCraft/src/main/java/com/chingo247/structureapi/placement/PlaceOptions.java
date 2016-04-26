/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.placement;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.Region;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class PlaceOptions {
    
    private int cubeX;
    private int cubeY;
    private int cubeZ;
    private final List<BlockPredicate> toIgnore;
    private final List<BlockFilter> masks;
  
    public PlaceOptions() {
        this.cubeX = 16;
        this.cubeY = -1;
        this.cubeZ = 16;
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
    
    public void addIgnore(Iterable<? extends Region> regionsToIgnore) {
        for(final Region region : regionsToIgnore) {
            toIgnore.add(new BlockPredicate() {

                @Override
                public boolean evaluate(Vector position, Vector worldPosition, BaseBlock block) {
                    return (region.contains(worldPosition));
                }
            });
        }
    }

    /**
     * Returns all the BlockPredicates that will be determine if a block should be ignored
     * @return The blockPredicates
     */
    public List<BlockPredicate> getIgnore() {
        return toIgnore;
    }
    
    public List<BlockFilter> getBlockMasks() {
        return masks;
    } 
    
    public void addBlockMask(BlockFilter mask) {
        this.masks.add(mask);
    }
    
    public int getCubeX() {
        return cubeX;
    }

    public void setCubeX(int cubeX) {
        this.cubeX = cubeX;
    }

    public int getCubeY() {
        return cubeY;
    }

    public void setCubeY(int cubeY) {
        this.cubeY = cubeY;
    }

    public int getCubeZ() {
        return cubeZ;
    }

    public void setCubeZ(int cubeZ) {
        this.cubeZ = cubeZ;
    }
    
}
