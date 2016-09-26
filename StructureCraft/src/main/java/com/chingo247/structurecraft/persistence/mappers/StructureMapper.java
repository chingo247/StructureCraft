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

import com.chingo247.structurecraft.model.structure.ConstructionStatus;
import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.model.world.Spatial;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 *
 * @author Chingo247
 */
public class StructureMapper implements ResultSetMapper<Structure> {

    @Override
    public Structure map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Long structureId = r.getLong("id");
        String name = r.getString("name");
        Timestamp createdAt = r.getTimestamp("created_at");
        Timestamp deletedAt = r.getTimestamp("deleted_at");
        Timestamp completetAt = r.getTimestamp("completed_at");
        double refundValue = r.getDouble("refund_value");
        ConstructionStatus status = ConstructionStatus.match(r.getInt("structure_status"));
        long spatialId = r.getLong("id");
        int minx = r.getInt("min_x");
        int miny = r.getInt("min_y");
        int minz = r.getInt("min_z");
        int maxx = r.getInt("max_x");
        int maxy = r.getInt("max_y");
        int maxz = r.getInt("max_z");
        int originx = r.getInt("origin_x");
        int originy = r.getInt("origin_y");
        int originz = r.getInt("origin_z");
        int orientation = r.getInt("orientation");
        UUID worldUUID = UUID.fromString(r.getString("world_uuid"));
        String worldName = r.getString("world_name");
        Spatial spatial = new Spatial(spatialId,  minx, maxx, miny, maxy, minz, maxz, worldUUID, worldName, orientation);
        spatial.setOriginX(originx);
        spatial.setOriginY(originy);
        spatial.setOriginZ(originz);
        
        Structure structure = new Structure(spatial, name);
        structure.setCompletedAt(completetAt);
        structure.setCreatedAt(createdAt);
        structure.setDeletedAt(deletedAt);
        structure.setId(structureId);
        structure.setName(name);
        structure.setRefundValue(refundValue);
        structure.setStatus(status);
        return structure;
    }
    
}
