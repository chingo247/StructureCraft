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
import com.chingo247.structurecraft.model.structure.StructureDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 *
 * @author Chingo247
 */
public class StructureDetailsRowMapper implements ResultSetMapper<StructureDetails>{

    
    @Override
    public StructureDetails map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Long id = r.getLong("id");
        String name = r.getString("name");
        Timestamp createdAt = r.getTimestamp("created_at");
        Timestamp deletedAt = r.getTimestamp("deleted_at");
        Timestamp completetAt = r.getTimestamp("completed_at");
        ConstructionStatus status = ConstructionStatus.match(r.getInt("structure_status"));
        double refundValue = r.getDouble("refund_value");
        return new StructureDetails(id, name, status, refundValue, completetAt, createdAt, deletedAt);
    }
    
}
