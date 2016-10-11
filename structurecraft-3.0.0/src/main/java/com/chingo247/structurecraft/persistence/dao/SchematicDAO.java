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

import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 *
 * @author Chingo247
 */
public interface SchematicDAO {

    /**
     * Inserts the values into the schematics table.
     * @param hash The hash of the schematic, which is used as id
     * @param title The title of the schematic
     * @param width The width in blocks of the schematic
     * @param height The height in blocks of the schematic
     * @param length The length in blocks of the schematic
     * @param blocks The amount of blocks (non-air) in this schematic
     */
    @SqlUpdate("INSERT INTO `STRUCTURECRAFT`.`SCHEMATICS`(HASH_VALUE, DESCRIPTIVE_TITLE, BLOCKS_WIDTH, BLOCKS_HEIGHT,BLOCKS,LENGTH,NR_OF_BLOCKS) "
            +  "VALUES (:hash, :title, :width, :height, :length, :blocks) ")
    void insert(@Bind("hash") long hash, @Bind("title") String title, @Bind("width") int width, @Bind("height") int height, @Bind("length") int length, @Bind("blocks") int blocks);
    
    
    /**
     * Deletes a schematic from the database
     * @param hash The hash value of the schematic
     */
    @SqlUpdate("SELECT CASE WHEN EXISTS(SELECT 1 FROM `STRUCTURECRAFT`.`SCHEMATICS` S WHERE S.HASH_VALUE = :hash) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END") 
    boolean exists(@Bind("hash") long hash);
    
    
    /**
     * Deletes a schematic from the database
     * @param hash The hash value of the schematic
     */
    @SqlUpdate("DELETE FROM `STRUCTURECRAFT`.`SCHEMATICS` S WHERE S.HASH_VALUE = :hash")
    void delete(@Bind("hash") long hash);
    
    
    
}
