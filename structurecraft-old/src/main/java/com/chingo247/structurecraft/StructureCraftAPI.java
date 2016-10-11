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
package com.chingo247.structurecraft;

import com.chingo247.structurecraft.persistence.repositories.SpatialRepository;
import com.chingo247.structurecraft.persistence.repositories.StructureRepository;
import com.chingo247.structurecraft.services.ServicesManager;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author Chingo
 */
public class StructureCraftAPI {
    
    public static DBI getDBI() {
        return ServicesManager.IMP.getDBI();
    }
    
    public static StructureRepository newStructureRepository(Handle h) {
        StructureRepository repository = new StructureRepository(h, ServicesManager.IMP.getDBIProvider().useSpatialIndex());
        return repository;
    }
    
    public static SpatialRepository newSpatialRepository(Handle h) {
        SpatialRepository repository = new SpatialRepository(h, ServicesManager.IMP.getDBIProvider().useSpatialIndex());
        return repository;
    }
    
    
    
}
