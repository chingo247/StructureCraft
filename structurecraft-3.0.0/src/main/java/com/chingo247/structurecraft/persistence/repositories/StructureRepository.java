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

import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.persistence.dao.StructureDAO;
import org.skife.jdbi.v2.Handle;

/**
 * CRUD Operations for structures
 * @author Chingo247
 */
public class StructureRepository {
    
    private Handle handle;
    private SpatialRepository spatialRepository;
    private StructureDAO structureDAO;
    
    /**
     * Constructor.
     */
    public StructureRepository(Handle handle, boolean useSpatialIndex) {
        this.handle = handle;
        this.structureDAO = handle.attach(StructureDAO.class);
        this.spatialRepository = new SpatialRepository(handle, useSpatialIndex);
    }
    
    
    
    /**
     * Adds a structure to the database.
     * @param structure The structure to add
     */
    public long add(Structure structure) {
        Spatial sp = structure.getSpatial();
        sp.setType("STRUCTURE");
        long spatial = spatialRepository.insert(sp);
        long sid =  structureDAO.insert(structure.getName(), structure.getRefundValue(), structure.getStatus().getStatusId(), 
                structure.getCreatedAt(), structure.getDeletedAt(), structure.getCompletedAt(), 
                spatial
        );
        return sid;
    }
    
    /**
     * Finds a structure by it's id
     * @param id The id of the structure
     * @return The structure with the corresponding id
     */
    public Structure find(long id) {
        return structureDAO.findById(id);
    }
    
    /**
     * Updates an entire structure
     * @param structure The structure to update
     */
    public void update(Structure structure) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Deletes a structure
     * @param id The id of the structure
     */
    public void delete(long id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
