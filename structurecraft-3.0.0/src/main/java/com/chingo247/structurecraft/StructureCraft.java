/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft;

import com.chingo247.structurecraft.io.plans.StructurePlanStorage;
import com.chingo247.structurecraft.io.schematic.SchematicStorage;
import com.chingo247.structurecraft.xplatform.core.server.APlatform;
import com.chingo247.structurecraft.xplatform.core.server.IPlugin;
import com.sun.webkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Chingo
 */
public class StructureCraft {
    
    public static final StructureCraft IMP = new StructureCraft();
    
    private IPlugin structurecraftPlugin;
    private StructureCraftConfig config;
    private APlatform platform;
    private SchematicStorage schematicStorage;
    
    private StructureCraft() {
    }
    
    public void registerStructureCraftPlugin(IPlugin plugin) throws IOException, StructureCraftException {
        if(this.structurecraftPlugin != null) {
            throw new StructureCraftException("Already registered a plugin");
        }
        this.structurecraftPlugin = plugin;
        this.platform = plugin.getPlatform();
        
        // Setup the config
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = new StructureCraftConfig();
        config.load(configFile);
        
        // Setup the schematic storage (for internal usage)
        File schematicsDir = new File(plugin.getDataFolder(), "schematics");
        schematicsDir.mkdirs();
        this.schematicStorage = new SchematicStorage(schematicsDir , 0);
        
        
    }

    public IPlugin getPlugin() {
        return structurecraftPlugin;
    }
    
    public APlatform getPlatform() {
        return platform;
    }
    
    public StructureCraftConfig getConfig() {
        return config;
    }

    public SchematicStorage getSchematicStorage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public StructurePlanStorage getStructurePlansStorage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isLoadingPlans() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
