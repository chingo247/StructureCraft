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
package com.chingo247.structurecraft.persistence.dao;

import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.persistence.mappers.StructureMapper;
import com.sk89q.worldedit.Vector;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 *
 * @author Chingo247
 */
public interface StructureDAO {
    
   
    @GetGeneratedKeys
    @SqlUpdate("insert into STRUCTURES (structure_status,name,refund_value,completed_at,created_at,deleted_at,spatial_id) "
            +  "values (:status,:name,:refund_value,:completed_at, :created_at, :deleted_at, :spatial_id)")
    long insert(@Bind("name") String name, @Bind("refund_value") double value, @Bind("status") int status, 
                @Bind("created_at") Timestamp created_at, @Bind("deleted_at") Timestamp deleted_at, 
                @Bind("completed_at") Timestamp completed_at, @Bind("spatial_id") long spatial_id
    );
    
    @SqlUpdate("update STRUCTURES SET name = :name, refund_value = :refund_value, structure_status = :status, "
            +  "created_at = :created_at, deleted_at = :deleted_at, completed_at = :completed_at")
    void update(@Bind("name") String name, @Bind("refund_value") double value, @Bind("status") int status, 
                @Bind("created_at") Timestamp created_at, @Bind("deleted_at") Timestamp deleted_at, 
                @Bind("completed_at") Timestamp completed_at
    );
    
    
    @SqlQuery("select S.* from STRUCTURES S "
            + "where S.ID = :sid")
    @Mapper(StructureMapper.class)
    Structure findById(@Bind("id") long id);
    
    //TODO NOT YET IMPLEMENTED
    List<Structure> getTree(@Bind("id") long id, @Bind("down") boolean traverseDown);

    //TODO NOT YET IMPLEMENTED
    Structure findStructureOnPosition(UUID uuid, Vector pos1);
    
}
