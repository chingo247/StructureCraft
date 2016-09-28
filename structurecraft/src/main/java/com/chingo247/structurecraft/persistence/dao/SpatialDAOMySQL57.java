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
package com.chingo247.structurecraft.persistence.dao;

import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.persistence.mappers.SpatialMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 *
 * @author Chingo
 */
public interface SpatialDAOMySQL57 extends SpatialDAO {
    
    
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO SPATIALS (origin_x, origin_y, origin_z, orientation, min_y,max_y, envelope, spatial_type, world_uuid) "
             + "VALUES (:origin_x, :origin_y, :origin_z, :orientation, :min_y, :max_y, (ST_MakeEnvelope(point(:min_x, :min_z), point(:max_x, :max_z))), :spatial_type, :worlduuid)")
    @Override
    long insert(@Bind("origin_x") int origin_x, @Bind("origin_y") int origin_y, @Bind("origin_z") int origin_z, 
                @Bind("orientation") int orientation,  @Bind("min_x") int min_x,  @Bind("min_y") int min_y,  @Bind("min_z") int min_z, 
                @Bind("max_x")int max_x, @Bind("max_y") int max_y, @Bind("max_z") int max_z, @Bind("spatial_type") String spatial_type, @Bind("worlduuid") String worlduuid);
    
    @Mapper(SpatialMapper.class)
    @SqlQuery("SELECT sp.id, sp.origin_x, sp.origin_y, sp.origin_z, sp.orientation, sp.min_y, sp.max_y, astext(sp.envelope), sp.world_uuid "
            + "FROM `structurecraft`.`spatials` SP "
            + "WHERE sp.id = :id ")
    @Override
    Spatial findById(@Bind("id") long id);
    
    
}
