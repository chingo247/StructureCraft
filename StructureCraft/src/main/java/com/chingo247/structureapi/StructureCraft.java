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
package com.chingo247.structureapi;

import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.xplatform.core.IPlugin;
import java.io.File;

/**
 *
 * @author Chingo
 */
public class StructureCraft {
    
    private static StructureCraft instance;
    private IPlugin plugin;
    
    private StructureCraft() {
    }
    
    public static StructureCraft instance() {
        if(instance == null) {
            instance = new StructureCraft();
        }
        return instance;
    }
    
    public void registerStructureCraft(IPlugin plugin) throws Exception {
        if(this.plugin != null) {
            throw new Exception("Already registered");
        }
        this.plugin = plugin;
    }
    
    public File getWorkingDirectory() {
        return plugin.getDataFolder();
    }
    
    public File getDirectory(Structure structure) {
        File worldDirector = new File(getWorkingDirectory(), "worlds/" + structure.getWorldName() + "/");
        File structuresDir =  new File(worldDirector, "structures/" + structure.getId() + "/");
        structuresDir.mkdirs();
        return structuresDir;
    }
    
    
    
}
