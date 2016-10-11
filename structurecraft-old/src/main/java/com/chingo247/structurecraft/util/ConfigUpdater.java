/*
 * Copyright (C) 2016 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.structurecraft.util;


import com.chingo247.structurecraft.exception.StructureCraftException;
import com.chingo247.structurecraft.services.config.ConfigProvider;
import com.chingo247.structurecraft.util.VersionUtil;
import com.chingo247.structurecraft.util.VersionUtil;
import com.chingo247.structurecraft.util.yaml.YAMLProcessor;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Chingo
 */
public abstract class ConfigUpdater {
    
    
    private File currentConfigFile;
    private File configFileToCheck;
    private boolean updateOnError = true;

    public ConfigUpdater(File currentConfig, File configToCheck) {
        this.currentConfigFile = currentConfig;
        this.configFileToCheck = configToCheck;
    }
    
    public abstract void onUpdate(String reason);
    
    public void update() throws StructureCraftException, IOException {
        if (currentConfigFile.exists()) {
            String newConfigVersion = ConfigProvider.getVersion(configFileToCheck);
            String currentConfigVersion = null;
            boolean needsUpdating = false;
            try {
                currentConfigVersion = ConfigProvider.getVersion(currentConfigFile);
            } catch (Exception ex) {
                YAMLProcessor processor = new YAMLProcessor(currentConfigFile, false);
                if(processor.getProperty("version") != null) {
                    throw new StructureCraftException("An error occurred while loading the config file, if the config file is missing expected values, try removing the file. When the file is removed, a new (default) config will be generated");
                } else {
                    needsUpdating = true;
                }
            }
            if(needsUpdating || (currentConfigVersion == null || (VersionUtil.compare(currentConfigVersion, newConfigVersion) == -1))) {
                int count = 1;
                String baseName = FilenameUtils.getBaseName(currentConfigFile.getName());
                File oldFile = new File(currentConfigFile.getParent(), baseName + "(" + count + ").old.yml");
                while(oldFile.exists()) {
                    count++;
                    oldFile = new File(currentConfigFile.getParent(), baseName + "(" + count + ").old.yml");
                }
                
                String reason;
                if(needsUpdating || (currentConfigVersion == null)) {
                    reason = "No 'version' value found in config";
                } else {
                    reason = "Older 'version' value found in config.yml";
                }
                
                onUpdate(reason);
                
                FileUtils.copyFile(currentConfigFile, oldFile);
                FileUtils.copyFile(configFileToCheck, currentConfigFile);
            }
        } else {
            FileUtils.copyFile(configFileToCheck, currentConfigFile);
        }
        
        
        
    }
    
}
