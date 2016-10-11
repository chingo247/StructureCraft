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

import com.chingo247.structurecraft.model.entities.Settler;
import com.chingo247.structurecraft.persistence.mappers.SettlerMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 *
 * @author Chingo
 */
public interface SettlerDAO {
    
    @GetGeneratedKeys
    @SqlUpdate("insert into `STRUCTURECRAFT`.`SETTLERS` (name, uuid, is_player) values (:name, :uuid, :is_player)")
    long insert(@Bind("name") String name, @Bind("uuid") String uuid, @Bind("is_player") boolean is_player);
    
    @SqlUpdate("UPDATE `STRUCTURECRAFT`.`SETTLERS` S SET S.name = :name WHERE S.uuid = :uuid")
    void updateName(@Bind("name") String name, @Bind("uuid") String uuid);
    
    @Mapper(SettlerMapper.class)
    @SqlQuery("SELECT * FROM `STRUCTURECRAFT`.`SETTLERS` S WHERE S.id = :id")
    Settler findById(@Bind("id") long id);
    
    @Mapper(SettlerMapper.class)
    @SqlQuery("SELECT * FROM `STRUCTURECRAFT`.`SETTLERS` S WHERE S.uuid = :uuid")
    Settler findByUUID(@Bind("uuid") String UUID);
    
    @SqlUpdate("DELETE FROM `STRUCTURECRAFT`.`SETTLERS` WHERE id = :id")
    void delete(@Bind("id") long id);
    
}
