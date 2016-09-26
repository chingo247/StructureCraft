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
package com.chingo247.structurecraft.persistence.mappers;

import com.chingo247.structurecraft.model.world.Spatial;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 *
 * @author Chingo247
 */
public class DefaultSpatialMapper implements ResultSetMapper<Spatial>{

    @Override
    public Spatial map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        long id = r.getLong("id");
        int min_x = r.getInt("min_x");
        int min_y = r.getInt("min_y");
        int min_z = r.getInt("min_z");
        int max_x = r.getInt("max_x");
        int max_y = r.getInt("max_y");
        int max_z = r.getInt("max_z");
        int origin_x = r.getInt("origin_x");
        int origin_y = r.getInt("origin_y");
        int origin_z = r.getInt("origin_z");
        int orientation = r.getInt("orientation");
        UUID worldUUID = UUID.fromString(r.getString("world_uuid"));
        String worldName = r.getString("world_name");
        
        Spatial sp =  new Spatial(id, min_x, max_x, min_y, max_y, min_z, max_z, worldUUID, worldName, orientation);
        sp.setOriginX(origin_x);
        sp.setOriginY(origin_y);
        sp.setOriginZ(origin_z);
        return sp;
    }
    
}
