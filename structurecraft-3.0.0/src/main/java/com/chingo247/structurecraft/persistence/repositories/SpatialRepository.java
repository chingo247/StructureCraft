/*
 * Copyright (C) 2016 Chingo247
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
package com.chingo247.structurecraft.persistence.repositories;

import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.persistence.dao.SpatialDAO;
import com.chingo247.structurecraft.persistence.dao.SpatialDAOMySQL57;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.List;
import java.util.UUID;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author Chingo247
 */
public class SpatialRepository {
    
    private SpatialDAO spatialDAO;
    private Handle handle;

    public SpatialRepository(Handle handle, boolean useSpatialIndex) {
        this.handle = handle;
        this.spatialDAO = useSpatialIndex ? handle.attach(SpatialDAOMySQL57.class) : handle.attach(SpatialDAO.class);
    }
    
    public long insert(Spatial sp) {
        return spatialDAO.insert(sp.getOriginX(), sp.getOriginY(), sp.getOriginY(), sp.getOriginZ(), sp.getMinX(), sp.getMinY(), sp.getMinZ(), sp.getMaxX(), sp.getMaxY(), sp.getMaxZ(), sp.getStructureType(), sp.getWorldUUID().toString(), sp.getWorld());
    }
    
    public Spatial findById(long id) {
        return this.spatialDAO.findById(id);
    }
    
    public void delete(long id) {
        this.spatialDAO.delete(id);
    }
    
    public boolean overlaps(Spatial spatial) {
        return overlaps(spatial.getWorldUUID(), spatial.getCuboid());
    }
    
    public boolean overlaps(UUID world, CuboidRegion region) {
        Vector min = region.getMinimumPoint(), max = region.getMaximumPoint();
        return this.spatialDAO.hasSpatialsWithin(world.toString(), min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
    }
    
    public List<Spatial> findWithin(Spatial spatial) {
        return findWithin(spatial.getWorldUUID(), spatial.getCuboid());
    }
    
    public List<Spatial> findWithin(UUID world, CuboidRegion region) {
        Vector min = region.getMinimumPoint(), max = region.getMaximumPoint();
        return this.spatialDAO.findSpatialsWithin(world.toString(), min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());
    }
    
    
    
    
    
}
