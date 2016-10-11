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

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Chingo
 */
public interface StructureOwnershipDAO {
    
    @GetGeneratedKeys
    @SqlUpdate("insert into STRUCTURE_OWNERSHIPS (structure_id, settler_id, owner_type) values (:structure_id, :settler_id, :owner_type)")
    long insert(@Bind("structure_id") long structure_id, @Bind("settler_id") long settler_id, @Bind("owner_type") int owner_type);
    
    @SqlUpdate("update STRUCTURE_OWNERSHIPS set owner_type = :owner_type where structure_id = :structure_id and settler_id = :settler_id")
    void updateType(@Bind("structure_id") long structure_id, @Bind("settler_id") long settler_id, @Bind("owner_type") int owner_type);
    
    
    
}
