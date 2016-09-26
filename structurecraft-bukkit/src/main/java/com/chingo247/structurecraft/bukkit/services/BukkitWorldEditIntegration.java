/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.bukkit.services;

import com.chingo247.structurecraft.services.plugins.IWorldEditIntegration;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class BukkitWorldEditIntegration implements IWorldEditIntegration {
    
    private WorldEditPlugin worldEdit;

    public BukkitWorldEditIntegration(WorldEditPlugin worldEdit) {
        this.worldEdit = worldEdit;
    }
    
    @Override
    public Player getPlayer(UUID uuid) {
        return worldEdit.wrapPlayer(Bukkit.getPlayer(uuid));
    }

    @Override
    public World getWorld(String world) {
        List<? extends World> worlds = WorldEdit.getInstance().getServer().getWorlds();
        for (World w : worlds) {
            if (w.getName().equals(world)) {
                return w;
            }
        }
        return null;
    }
    
}
