/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.xplatform.core.item;

import java.util.List;

/**
 *
 * @author Chingo
 */
public interface IItemStack {

    /**
     * Creates a clone of this item
     * @return The clone
     */
    AItemstack cloneItem();

    /*
     * Gets the amount of items on this stack
     * @return the Amount
     */
    int getAmount();

    /**
     * Gets the byte data of this item
     * @return The data
     */
    int getData();

    /**
     * Gets the lore of this Item
     * @return The lore
     */
    List<String> getLore();

    /**
     * Gets the material of this item
     * @return The material
     */
    int getMaterial();

    /**
     * Gets the name of the item
     * @return The name
     */
    String getName();

    /**
     * Checks if this item matches another
     * @param other The other item
     * @return True if item matches material, data and lore
     */
    boolean matches(AItemstack other);

    /**
     * Sets the amount of items on this stack
     * @param amount The amount
     */
    void setAmount(int amount);

    /**
     * Sets the lore of this item
     * @param lore The lore
     */
    void setLore(List<String> lore);

    /**
     * Sets the material of this item
     * @param material The material
     */
    void setMaterial(int material);

    /**
     * Sets the name of the item
     * @param name The name
     */
    void setName(String name);
    
}
