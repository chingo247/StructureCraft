/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.model.entities;

import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class Settler  {
    
    private Long id;
    private UUID uuid;
    private String name;
    private boolean isPlayer;
    
    public Settler(Long id, UUID uuid, String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }

    public void setIsPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }

    public boolean isPlayer() {
        return isPlayer;
    }
    
    public Long getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    
}
