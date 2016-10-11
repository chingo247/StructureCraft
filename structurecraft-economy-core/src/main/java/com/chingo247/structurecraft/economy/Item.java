/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.economy;

import java.util.List;

/**
 *
 * @author Chingo
 */
public class Item {
    
    private int material, data;
    private List<String> lore;

    public Item(int material, int data) {
        this.material = material;
        this.data = data;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
    
}
