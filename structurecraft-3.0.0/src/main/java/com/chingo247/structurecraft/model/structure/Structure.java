/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.structurecraft.model.structure;

import com.chingo247.structurecraft.StructureCraft;
import com.chingo247.structurecraft.exception.StructurePlanException;
import com.chingo247.structurecraft.io.plans.StructurePlanReader;
import com.chingo247.structurecraft.model.plans.StructurePlan;
import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.util.XXHasher;
import com.chingo247.structurecraft.xplatform.core.IWorld;
import com.sk89q.worldedit.Vector2D;
import java.io.File;

/**
 * As opposed to the {@link StructureNode} this unmodifable structure has all it's properties loaded. 
 * None of the operations of this class have to be executed within a transaction
 * @author Chingo
 */
public class Structure extends StructureDetails {
    
    private static final XXHasher HASHER = new XXHasher();
    public static final String ROLLBACK_DIRECTORY = "rollback";
    
    private final Spatial spatial;

    public Structure(Spatial spatial, String name) {
        this(null, spatial, name);
    }

    public Structure(Long id, Spatial spatial, String name) {
        super(id, name, ConstructionStatus.ON_HOLD, 0.0, null, null, null);
        this.spatial = spatial;
    }
    
    /**
     * Gets the spatial of this structure.
     * @return The spatial
     */
    public Spatial getSpatial() {
        return spatial;
    }

    /**
     * Gets the directory of this structure.
     * @return The directory of this structure
     */
    public File getDirectory() {
        if (getId() == null) {
            throw new IllegalStateException("Can't get directory of unsaved structure! Structure needs to be stored in database and have an ID");
        }
        File craftdir = StructureCraft.IMP.getPlugin().getDataFolder();
        IWorld world = StructureCraft.IMP.getPlatform().getServer().getWorld(spatial.getWorldUUID());
        File worldDir = new File(craftdir, world.getName() + "_" + HASHER.hash64String(spatial.getWorldUUID().toString()));
        Vector2D region = spatial.getRegion();
        File dir = new File(worldDir, region.getBlockX() + "_" + region.getBlockZ() + "/" + getId());
        dir.mkdirs();
        return dir;
    }
   
    public StructurePlan getStructurePlan() throws StructurePlanException {
        File planFile = new File(getDirectory(), "structureplan.xml");
        if(!planFile.exists()) {
            throw new StructurePlanException("Structure #" + getId() + " doesn't have a plan!");
        }

        StructurePlanReader loader = new StructurePlanReader();
        StructurePlan plan = loader.loadFile(planFile);

        return plan;
    }

}
