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
import com.chingo247.structurecraft.persistence.mappers.DefaultSpatialMapper;
import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 *
 * @author Chingo
 */
public interface SpatialDAO {
    
    /**
     * Inserts a spatial
     * @param origin_x
     * @param origin_y
     * @param origin_z
     * @param orientation
     * @param min_x
     * @param min_y
     * @param min_z
     * @param max_x
     * @param max_y
     * @param max_z
     * @param spatial_type
     * @param worlduuid
     * @return The generated id of the spatial
     */
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO `STRUCTURECRAFT`.`SPATIALS` (origin_x, origin_y, origin_z, orientation, min_x, min_y, min_z, max_x, max_y, max_z, spatial_type, world_uuid, world_name) "
             + "VALUES (:origin_x, :origin_y, :origin_z, :orientation, :min_x, :min_y, :min_z, :max_x, :max_y, :max_z, :spatial_type, :worlduuid, :worldname)")
    long insert(@Bind("origin_x") int origin_x, @Bind("origin_y") int origin_y, @Bind("origin_z") int origin_z, 
                @Bind("orientation") int orientation,  @Bind("min_x") int min_x,  @Bind("min_y") int min_y,  @Bind("min_z") int min_z, 
                @Bind("max_x")int max_x, @Bind("max_y") int max_y, @Bind("max_z") int max_z, @Bind("spatial_type") String spatial_type, @Bind("worlduuid") String worlduuid, @Bind("worldname") String worldName);
    
    
    
    @SqlUpdate("DELETE FROM `STRUCTURECRAFT`.`SPATIALS` S WHERE S.id = :id")
    void delete(@Bind("id") long id);
    
    @SqlQuery("SELECT * FROM `STRUCTURECRAFT`.`SPATIALS` S WHERE S.id = :id")
    @Mapper(DefaultSpatialMapper.class)
    Spatial findById(@Bind("id") long id);
    
    @SqlQuery("SELECT * FROM `STRUCTURECRAFT`.`SPATIALS` S "
            + "WHERE s.world_uuid = :world_uuid "
            + "AND S.max_x >= :min_x "
            + "AND S.min_x <= :max_x "
            + "AND S.max_y >= :min_y "
            + "AND S.min_y <= :max_y "
            + "AND S.max_z >= :min_z "
            + "AND S.min_z <= :max_z")
    @Mapper(DefaultSpatialMapper.class)
    List<Spatial> findSpatialsWithin(@Bind("world_uuid") String worldUUID, @Bind("min_x") int minX, @Bind("min_y") int minY, @Bind("min_z") int minZ, @Bind("max_x") int maxX, @Bind("max_y") int maxY, @Bind("max_z") int maxZ);
    
    @SqlQuery("SELECT EXISTS (SELECT 1 FROM `STRUCTURECRAFT`.`SPATIALS` S "
            + "WHERE s.world_uuid = :world_uuid "
            + "AND S.max_x >= :min_x "
            + "AND S.min_x <= :max_x "
            + "AND S.max_y >= :min_y "
            + "AND S.min_y <= :max_y "
            + "AND S.max_z >= :min_z "
            + "AND S.min_z <= :max_z )")
    boolean hasSpatialsWithin(@Bind("world_uuid") String worldUUID, @Bind("min_x") int minX, @Bind("min_y") int minY, @Bind("min_z") int minZ, @Bind("max_x") int maxX, @Bind("max_y") int maxY, @Bind("max_z") int maxZ);
    
    
}
