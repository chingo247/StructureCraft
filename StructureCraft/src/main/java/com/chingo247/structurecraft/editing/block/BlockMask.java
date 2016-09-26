/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.editing.block;

import com.sk89q.worldedit.blocks.BaseBlock;

/**
 *
 * @author Chingo
 */
public class BlockMask {
    
    private BlockPredicate predicate;
    private BaseBlock changeTo;

    public BlockMask(BlockPredicate predicate, BaseBlock changeTo) {
        this.predicate = predicate;
        this.changeTo = changeTo;
    }
    
    
    /**
     * Applies the BlockMask
     * @param relativePosition The relative position
     * @param worldPosition The position in the world
     * @param block The block that will be placed
     * @return The BaseBlock that has been applied
     */
    public boolean mask(int blockX, int blockY, int blockZ, int worldX, int worldY, int worldZ, BaseBlock block) {
        if(predicate.evaluate(blockX, blockY, blockZ, worldX, worldY, worldZ, block)) {
            block.setId(changeTo.getId());
            block.setData(changeTo.getData());
            if(changeTo.getNbtData() != null) {
                block.setNbtData(changeTo.getNbtData());
            }
            return true;
        }
        return false;
    }
    
}
