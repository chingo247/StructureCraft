/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.structurecraft.io.schematic;

import static com.chingo247.structurecraft.io.blockstore.NBTUtils.getChildTag;
import com.chingo247.structurecraft.model.container.FastClipboard;
import com.chingo247.structurecraft.util.XXHasher;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.io.IOException;
import com.google.common.base.Preconditions;
import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NamedTag;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Chingo
 */
public class Schematic implements ISchematic {

    private final File schematicFile;
    private final long hash;
    private final int width;
    private final int height;
    private final int length;
    private final short[] ids;
    private final byte[] data;
    private int yAxisOffset;
    private Map<BlockVector,CompoundTag> tileEntities;
    private boolean[] tiles; // for instant lookups

    Schematic(File schematicFile, int width, int height, int length, int axisOffset, long hash, short[] ids, byte[] data, Map<BlockVector, CompoundTag> tileEntities, boolean[] tiles) {
        Preconditions.checkNotNull(schematicFile);
        Preconditions.checkArgument(schematicFile.exists());
        this.schematicFile = schematicFile;
        this.yAxisOffset = axisOffset;
        this.hash = hash;
        this.width = width;
        this.height = height;
        this.length = length;
        this.tileEntities = tileEntities;
        this.tiles = tiles;
        this.ids = new short[width * height * length];
        this.data = new byte[width * height * length];
    }
    
    private int index(int x, int y, int z) {
        return (y * getWidth() * getLength()) + z * getWidth() + x;
    }
    
    public short getBlockId(int x, int y, int z) {
        return ids[index(x, y, z)];
    }
    
    public byte getData(int x, int y, int z) {
        return data[index(x, y, z)];
    }
    
    public CompoundTag getTile(int x, int y, int z) {
        if (tiles != null) {
            return tiles[index(x, y, z)] ? tileEntities.get(new BlockVector(x, y, z)) : null;
        } else {
            return tileEntities.get(new BlockVector(x,y,z));
        }
    }

    @Override
    public File getFile() {
        return schematicFile;
    }

    @Override
    public long getHash() {
        return this.hash;
    }

    @Override
    public final FastClipboard getClipboard() {
        if (!schematicFile.exists()) {
            throw new RuntimeException("File: " + schematicFile.getAbsolutePath() + " doesn't exist");
        }
        try {
            return FastClipboard.read(schematicFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Vector getSize() {
        return new Vector(width, height, length);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getAxisOffset() {
        return yAxisOffset;
    }

    public static SchematicHeader readHeader(File schematicFile) throws IOException {
        NamedTag rootTag = readRootTag(schematicFile);

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

    public static NamedTag readRootTag(File schematicFile) throws IOException {
        NamedTag rootTag;
        try (NBTInputStream nbtStream = new NBTInputStream(
                new GZIPInputStream(new FileInputStream(schematicFile)))) {
            rootTag = nbtStream.readNamedTag();
            if (!rootTag.getName().equalsIgnoreCase("Schematic")) {
                throw new RuntimeException("Tag 'Schematic' does not exist or is not first");
            }
        }
        return rootTag;
    }
    
    public static ISchematic readSchematic(File schematicFile) throws IOException {
        return readSchematic(schematicFile, false);
    }
    
    /**
     * Reads a schematic file
     * @param schematicFile The schematic file
     * @param instantLookups Whether an extra array should be used to mark tileEntities. <b>NOTE: uses extra memory</b>, but will decrease the execution time for {@link #getTile(int, int, int) }.
     * Therefore it's only beneficial when you expect to call that method a lot (like when placing a schematic).
     * 
     * @return The schematic
     * @throws IOException 
     */
    public static ISchematic readSchematic(File schematicFile, boolean instantLookups) throws IOException {
        
        NamedTag rootTag;
        try (NBTInputStream nbtStream = new NBTInputStream(
                new GZIPInputStream(new FileInputStream(schematicFile)))) {
            rootTag = nbtStream.readNamedTag();

            if (!rootTag.getName().equalsIgnoreCase("Schematic")) {
                throw new RuntimeException("Tag 'Schematic' does not exist or is not first");
            }
        }
        
        long hash = hash(schematicFile);
    

        // Check
        Map<String, Tag> schematic = (Map) rootTag.getTag().getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new RuntimeException("Schematic file is missing a \"Blocks\" tag");
        }

        // Get information
        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();
        byte[] ids = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] data = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
        byte[] addId = new byte[0];

        // We support 4096 block IDs using the same method as vanilla Minecraft, where
        // the highest 4 bits are stored in a separate byte array.
        if (schematic.containsKey("AddBlocks")) {
            addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
        }

        // read ids
        short[] blockids = new short[ids.length]; // Have to later combine IDs
        // Combine the AddBlocks data with the first 8-bit block ID
        for (int index = 0; index < ids.length; index++) {
            if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
                blockids[index] = (short) (ids[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blockids[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (ids[index] & 0xFF));
                } else {
                    blockids[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (ids[index] & 0xFF));
                }
            }

        }

        // Need to pull out tile entities
        List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class)
                .getValue();
        Map<BlockVector, CompoundTag> tileEntitiesMap
                = new HashMap<>();
       
        boolean[] tiles = instantLookups ? new boolean[width * height * length] : null;
        for (Tag tag : tileEntities) {
            if (!(tag instanceof CompoundTag)) {
                continue;
            }
            CompoundTag t = (CompoundTag) tag;

            int x = 0;
            int y = 0;
            int z = 0;

            Map<String, Tag> values = new HashMap<>();

            for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
                if (entry.getKey().equals("x")) {
                    if (entry.getValue() instanceof IntTag) {
                        x = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("y")) {
                    if (entry.getValue() instanceof IntTag) {
                        y = ((IntTag) entry.getValue()).getValue();
                    }
                } else if (entry.getKey().equals("z")) {
                    if (entry.getValue() instanceof IntTag) {
                        z = ((IntTag) entry.getValue()).getValue();
                    }
                }

                values.put(entry.getKey(), entry.getValue());
            }

            if (tiles != null) {
                int index = (y * width * length) + z * width + x;
                tiles[index] = true;
            }
            
            BlockVector vec = new BlockVector(x, y, z);
            tileEntitiesMap.put(vec, new CompoundTag(values));
        }

        Tag tag = schematic.get("SCyAxisOffset");

        int yAxisOffset;
        if (tag == null) {
            yAxisOffset = 0;
        } else {
            IntTag yAxisTag = (IntTag) tag;
            yAxisOffset = yAxisTag.getValue();
        }
        
        
        
        
        return new Schematic(schematicFile, width, height, length, yAxisOffset, hash, blockids, data, tileEntitiesMap, tiles);
    }

    public static long hash(File schematicFile) throws IOException {
        XXHasher hasher = new XXHasher();
        return hasher.hash64(schematicFile);
    }
}
