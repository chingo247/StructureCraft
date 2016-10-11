/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.xplatform.core.item;

/**
 *
 * @author Chingo
 */
public interface IInventory {

    /**
     * Adds adds an item to this inventory
     * @param itemStack The item to add
     */
    void addItem(IItemStack itemStack);

    /**
     * Clears the inventory
     */
    void clear();

    /**
     * Gets all the items in this inventory
     * @return The items
     */
    IItemStack[] getItems();

    /**
     * Checks if the inventory has the item
     * @param stack The item
     * @return True if inventory has the item
     */
    boolean hasItem(IItemStack stack);

    /**
     * Removes an item from the inventory
     * @param item The item to remove
     */
    void removeItem(IItemStack item);

    /**
     * Sets an item at a certain index
     * @param index The item to set
     * @param itemstack The ItemStack
     */
    void setItem(int index, IItemStack itemstack);
    
}
