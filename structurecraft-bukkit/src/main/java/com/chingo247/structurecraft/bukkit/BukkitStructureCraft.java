/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.bukkit;

import com.chingo247.structurecraft.StructureCraft;
import com.chingo247.structurecraft.bukkit.services.BukkitPermissionRegister;
import com.chingo247.structurecraft.bukkit.services.BukkitVaultEconomyProvider;
import com.chingo247.structurecraft.bukkit.services.BukkitWorldEditIntegration;
import com.chingo247.structurecraft.exception.StructureCraftException;
import com.chingo247.structurecraft.persistence.connection.SCMySQLDB;
import com.chingo247.structurecraft.services.ServiceAlreadyRegisteredException;
import com.chingo247.structurecraft.services.ServicesManager;
import com.chingo247.structurecraft.services.config.ConfigProvider;
import com.chingo247.structurecraft.util.JarUtil;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.xplatform.core.IScheduler;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlatform;
import com.chingo247.xplatform.platforms.bukkit.BukkitScheduler;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo247
 */
public class BukkitStructureCraft extends JavaPlugin implements IPlugin {

    private static final Logger LOG = Logger.getLogger(BukkitStructureCraft.class.getName());
    private static final String RESOURCES_PATH = "com/chingo247/resources/";

    public static final String MSG_PREFIX = "[StructureCraft]: ";
    private ConfigProvider config;
    
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println(MSG_PREFIX + " WorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        
        
        try {
            // Setup config
            createDefaultConfig();
            config = ConfigProvider.load(new File(getDataFolder(), "config.yml"));
            
            // Setup default menu
            JarUtil.createDefault(new File(getDataFolder(), "menu.xml"), getFile(), RESOURCES_PATH + "default/" + "menu.xml");
            
            StructureCraft.IMP.registerStructureCraft(this);
            StructureCraft.IMP.reloadPlans();
            
            // Register services
            if (Bukkit.getPluginManager().getPlugin("vault") != null) ServicesManager.IMP.setEconomy(new BukkitVaultEconomyProvider());
            ServicesManager.IMP.registerDBI(null);
            ServicesManager.IMP.registerWorldEdit(new BukkitWorldEditIntegration((WorldEditPlugin)Bukkit.getPluginManager().getPlugin("worldedit")));
            ServicesManager.IMP.registerPermissions(new BukkitPermissionRegister());
            
            
        } catch (StructureCraftException ex) {
            Bukkit.getConsoleSender().sendMessage(new String[]{
                ChatColor.RED + ex.getMessage(), ChatColor.RED + "Disabling SettlerCraft-StructureAPI"
            });
            this.setEnabled(false);
        } catch (IOException | ServiceAlreadyRegisteredException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        

    }
    
    private void createDefaultConfig() throws StructureCraftException, IOException {
        // Get current Config
        File configFile = new File(getDataFolder(), "config.yml");
        
        // Get temp config
        File temp = new File(getDataFolder(), "temp");
        temp.mkdirs();
        File newConfigFile = new File(temp, "config.yml");
        newConfigFile.delete();
        newConfigFile = new File(temp, "config.yml");
        JarUtil.createDefault(newConfigFile, getFile(), RESOURCES_PATH + "config.yml");

        // Perform update if necessary
        BukkitStructureCraftConfigUpdater updater = new BukkitStructureCraftConfigUpdater(configFile, newConfigFile);
        updater.update();
        
        // Delete the temp directory
        FileUtils.deleteDirectory(temp);
    }

    @Override
    public IScheduler getScheduler() {
        return new BukkitScheduler(Bukkit.getServer(), this);
    }

    @Override
    public APlatform getPlatform() {
        return new BukkitPlatform(Bukkit.getServer());
    }
    
    
}
