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
package com.chingo247.structureapi.io.schematic;

import static com.chingo247.structureapi.io.blockstore.NBTUtils.getChildTag;
import com.chingo247.structureapi.model.container.FastClipboard;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NamedTag;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.Tag;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Chingo
 */
public class SchematicReader {
    
    public SchematicHeader readHeader(File schematicFile) throws IOException {
        NamedTag rootTag;
        try (NBTInputStream nbtStream = new NBTInputStream(
                new GZIPInputStream(new FileInputStream(schematicFile)))) {
            rootTag = nbtStream.readNamedTag();
            if (!rootTag.getName().equalsIgnoreCase("Schematic")) {
                throw new RuntimeException("Tag 'Schematic' does not exist or is not first");
            }
        }

        // Check
        Map<String, Tag> schematic = (Map) rootTag.getTag().getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new RuntimeException("Schematic file is missing a \"Blocks\" tag");
        }

        // Get information
        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
        
        Tag tag = schematic.get("SCyAxisOffset");
        int yAxisOffset;
        if (tag == null) {
            yAxisOffset = 0;
        } else {
            IntTag yAxisTag = (IntTag) tag;
            yAxisOffset = yAxisTag.getValue();
        }
        
        return new SchematicHeader(width, height, length, yAxisOffset);
    }
    
    public ISchematic readSchematic(File schematicFile) throws IOException {
        SchematicHeader header = readHeader(schematicFile);
        return new Schematic(schematicFile, header.getWidth(), header.getHeight(), header.getLength(), header.getyAxisOffset());
    }
    
}
