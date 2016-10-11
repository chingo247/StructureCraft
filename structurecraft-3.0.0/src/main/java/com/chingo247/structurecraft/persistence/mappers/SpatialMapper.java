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
import com.chingo247.structurecraft.util.value.Tuple2Val;
import com.sk89q.worldedit.Vector2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 *
 * @author Chingo247
 */
public class SpatialMapper implements ResultSetMapper<Spatial>{

    @Override
    public Spatial map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        long id = r.getLong("id");
        int min_y = r.getInt("min_y");
        int max_y = r.getInt("max_y");
        int origin_x = r.getInt("origin_x");
        int origin_y = r.getInt("origin_y");
        int origin_z = r.getInt("origin_z");
        int orientation = r.getInt("orientation");
        UUID worldUUID = UUID.fromString(r.getString("world_uuid"));
        String worldName = r.getString("world_name");
        
        Tuple2Val<Vector2D,Vector2D> coordinates = parseEnvelope(r.getString("envelope"));
        Vector2D min = coordinates.getItem1(), max = coordinates.getItem2();
        
        String type = r.getString("spatial_type");
        
        Spatial sp =  new Spatial(id, min.getBlockX(), min_y, min.getBlockZ(), max.getBlockX(), max_y, max.getBlockZ(), worldUUID, worldName, orientation, type);
        sp.setOriginX(origin_x);
        sp.setOriginY(origin_y);
        sp.setOriginZ(origin_z);
        return sp;
    }
    
    private static Tuple2Val<Vector2D,Vector2D> parseEnvelope(String polygonString) {
        String[] coordinates = polygonString.substring("POLYGON((".length(), polygonString.length() - 2).split(",");
        String[] minCor = coordinates[0].split("\\s");
        String[] maxCor = coordinates[2].split("\\s");
        
        int minX = Integer.parseInt(minCor[0].trim());
        int minZ = Integer.parseInt(minCor[1].trim());
        
        int maxX = Integer.parseInt(maxCor[0].trim());
        int maxZ = Integer.parseInt(maxCor[1].trim());
        return new Tuple2Val<>(new Vector2D(minX, minZ), new Vector2D(maxX, maxZ));
        
    }
    
    public static void main(String[] args) {
        parseEnvelope("POLYGON((11 33,44 33,44 66,11 66,11 33))");
    }
    
    
}
