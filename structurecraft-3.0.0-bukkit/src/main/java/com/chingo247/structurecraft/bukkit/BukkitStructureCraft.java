/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.bukkit;

import com.chingo247.structurecraft.xplatform.bukkit.BukkitPlatform;
import com.chingo247.structurecraft.xplatform.bukkit.BukkitScheduler;
import com.chingo247.structurecraft.xplatform.core.server.APlatform;
import com.chingo247.structurecraft.xplatform.core.server.IPlugin;
import com.chingo247.structurecraft.xplatform.core.server.IScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 *
 * @author Chingo247
 */
public class BukkitStructureCraft extends JavaPlugin implements IPlugin {

    private static final Logger LOG = Logger.getLogger(BukkitStructureCraft.class.getName());
    private static final String RESOURCES_PATH = "com/chingo247/resources/";

    public static final String MSG_PREFIX = "[StructureCraft]: ";
//    private ConfigProvider config;
    
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println(MSG_PREFIX + " WorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        
        
        
        
//        
//        
//        try {
//            // Setup config
//            createDefaultConfig();
//            config = ConfigProvider.load(new File(getDataFolder(), "config.yml"));
//            
//            // Setup default menu
//            JarUtil.createDefault(new File(getDataFolder(), "menu.xml"), getFile(), RESOURCES_PATH + "default/" + "menu.xml");
//            
//            StructureCraft.IMP.registerStructureCraft(this);
//            StructureCraft.IMP.reloadPlans();
//            
//            // Register services
//            if (Bukkit.getPluginManager().getPlugin("vault") != null) ServicesManager.IMP.setEconomy(new BukkitVaultEconomyProvider());
//            ServicesManager.IMP.registerDBIProvider(new SCMySQLDB("localhost", 3306, "root", "root"));
//            ServicesManager.IMP.registerWorldEdit(new BukkitWorldEditIntegration((WorldEditPlugin)Bukkit.getPluginManager().getPlugin("worldedit")));
//            ServicesManager.IMP.registerPermissions(new BukkitPermissionRegister());
//            
//            
//        } catch (StructureCraftException ex) {
//            Bukkit.getConsoleSender().sendMessage(new String[]{
//                ChatColor.RED + ex.getMessage(), ChatColor.RED + "Disabling SettlerCraft-StructureAPI"
//            });
//            this.setEnabled(false);
//        } catch (IOException | ServiceAlreadyRegisteredException ex) {
//            LOG.log(Level.SEVERE, ex.getMessage(), ex);
//        }
//        

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
