/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft;

import com.sk89q.util.yaml.YAMLFormat;
import com.sk89q.util.yaml.YAMLProcessor;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Chingo
 */
public class StructureCraftConfig {
    
    private boolean menuEnabled = false;
    private boolean shopEnabled = false;
    private boolean useHolograms = false;
    private boolean allowsSubstructures = false;
    private boolean protectStructures = false;
    private boolean protectConstructionZones = false;
    private boolean allowStructures = false;
    private boolean restrictedToZones = false;
    private boolean demolishIsRollback = false;
    
    private String version;
    private File file;

    public StructureCraftConfig() {
    }
    
    public void load(File file) throws IOException, StructureCraftException {
        YAMLProcessor yamlp = new YAMLProcessor(file, true, YAMLFormat.EXTENDED);
        yamlp.load();
        
        this.version = yamlp.getString("version", null);
        this.shopEnabled = getValue(yamlp, "structures.allow-substructures", Boolean.class);
        this.useHolograms = getValue(yamlp, "structures.use-holograms", Boolean.class);
        this.protectStructures = getValue(yamlp, version, Boolean.class);
        this.menuEnabled = getValue(yamlp, "menus.planmenu-enabled", Boolean.class);
        this.shopEnabled = getValue(yamlp, "menus.planshop-enabled", Boolean.class);
        this.demolishIsRollback = getValue(yamlp, "structures.demolish-is-rollback", Boolean.class);
        
        
    }
    
    private static <T> T getValue(YAMLProcessor processor, String path, Class<T> expectedValue) throws StructureCraftException {
        
        Object value = processor.getProperty(path);
        if(value != null) {
            try {
                return expectedValue.cast(value);
            } catch(ClassCastException ex) {
                
                if(expectedValue.getClass().isAssignableFrom(Boolean.class)) {
                   throw new StructureCraftException("Invalid value in config path '"+path+"': Expected 'true' or 'false' "+path+"'");
                }
                if(expectedValue.getClass().isAssignableFrom(Integer.class)) {
                   throw new StructureCraftException("Invalid value in config path '"+path+"': Expected a round number' "+path+"'");
                }
                if(expectedValue.getClass().isAssignableFrom(Double.class)) {
                   throw new StructureCraftException("Invalid value in config path: Expected a round number at '"+path+"'");
                }
                throw new StructureCraftException("Invalid value in config at: '"+path+"'");
                
            }
        } else {
            throw new StructureCraftException("Missing '" + path + "' in config!");
        }
        
        
    }
    
    
}
