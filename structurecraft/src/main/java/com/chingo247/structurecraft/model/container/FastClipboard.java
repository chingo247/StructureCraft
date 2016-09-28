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
package com.chingo247.structurecraft.model.container;

import com.chingo247.structurecraft.model.block.Block;
import com.google.common.base.Preconditions;
import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.jnbt.NamedTag;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Chingo
 * @deprecated Use the {@link Schematic} instead
 */
@Deprecated
public class FastClipboard implements IBlockContainer<CuboidRegion> {

    private final int width;
    private final int height;
    private final int length;
    private final short[] blockIds;
    private final byte[] data;
    private Map<BlockVector, Map<String,Tag>> tileEntities;
    private int yAxisOffset;

    public FastClipboard(int width, int height, int length, short[] blockIds, byte[] data) {
//        this.tileEntities = tileEntities;
        this.width = width;
        this.height = height;
        this.length = length;
        this.blockIds = blockIds;
        this.data = data;
        this.yAxisOffset = 0;
    }

    protected int index(int x, int y, int z) {
        return (y * getWidth() * getLength()) + z * getWidth() + x;
    }

    protected int index(Vector pos) {
        return (pos.getBlockY() * getWidth() * getLength()) + pos.getBlockZ() * getWidth() + pos.getBlockX();
    }
    
    public void setTileEntities(Map<BlockVector, Map<String,Tag>> tileEntities) {
        this.tileEntities = tileEntities;
    }

    public FastClipboard() {
        this.width = 0;
        this.height = 0;
        this.length = 0;
        this.blockIds = null;
        this.data = null;
    }
    
    /**
     * Sets the y-axis offset
     * @param yAxisOffset The y-axis offset to set. Note: number has to be a multiple of 90
     */
    public void setYAxisOffset(int yAxisOffset) {
        Preconditions.checkArgument(yAxisOffset % 90 == 0, "Axis offset has to be a multiple of 90");
        this.yAxisOffset = yAxisOffset;
    }
    

    /**
     * Gets the rotation offset over the y-axis
     *
     * @return
     */
    public int getyAxisOffset() {
        return yAxisOffset;
    }

    public Block getBlock(BlockVector pos) {
        return getBlock(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    @Override
    public boolean hasBlock(int x, int y, int z) {
        return (index(x, y, z)) < blockIds.length;
    }

    public Block getBlock(int x, int y, int z) {
//        int index = (y * width * length) + z * width + x;
        Block b = new Block(x, y, z, index(x, y, z), (short) data[index(x, y, z)]);

        Map<String, Tag> tileMap = tileEntities.get(new BlockVector(x, y, z));
        if (tileMap != null) {
            b.setNbtData(new CompoundTag(tileMap));
        }

        return b;
    }

    @Override
    public CuboidRegion getRegion() {
        return new CuboidRegion(Vector.ZERO, new BlockVector(width, height, length));
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public Vector getSize() {
        return new Vector(width, height, length);
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items The parent tag map
     * @param key The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws DataException if the tag does not exist or the tag is not of the
     * expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key,
            Class<T> expected) {

        if (!items.containsKey(key)) {
            throw new RuntimeException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new RuntimeException(
                    key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }

    public static FastClipboard read(File schematicFile) throws IOException {
        NBTInputStream nbtStream = new NBTInputStream(
                new GZIPInputStream(new FileInputStream(schematicFile)));

        NamedTag rootTag = nbtStream.readNamedTag();

        if (!rootTag.getName().equalsIgnoreCase("Schematic")) {
            throw new RuntimeException("Tag 'Schematic' does not exist or is not first");
        }
        nbtStream.close();

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
        Map<BlockVector, Map<String,Tag>> tileEntitiesMap
                = new HashMap<>();

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

            BlockVector vec = new BlockVector(x, y, z);
            tileEntitiesMap.put(vec, values);
        }

        Tag tag = schematic.get("SCyAxisOffset");

        int yAxisOffset;
        if (tag == null) {
            yAxisOffset = 0;
        } else {
            IntTag yAxisTag = (IntTag) tag;
            yAxisOffset = yAxisTag.getValue();
        }

        FastClipboard clipboard = new FastClipboard(width, height, length, blockids, data);
        clipboard.setTileEntities(tileEntitiesMap);
        clipboard.setYAxisOffset(yAxisOffset);
        
        return clipboard;
    }

    public static void rotateAndWrite(File schematicFile, int degree) throws IOException {
        if (degree % 90 != 0) {
            throw new IllegalArgumentException("Degree must be a multiple of 90");
        }
        NBTInputStream nbtStream = new NBTInputStream(
                new GZIPInputStream(new FileInputStream(schematicFile)));

        NamedTag rootTag = nbtStream.readNamedTag();

        if (!rootTag.getName().equalsIgnoreCase("Schematic")) {
            throw new RuntimeException("Tag 'Schematic' does not exist or is not first");
        }
        nbtStream.close();

        Map<String, Tag> schematic = (Map) rootTag.getTag().getValue(); // Because this is an unmodifiable instance....
        Tag currentYOffSetTag = schematic.get("SCyAxisOffset");

        int currentYOffSet;
        if (currentYOffSetTag == null) {
            currentYOffSet = 0;
        } else {
            IntTag yAxisTag = (IntTag) currentYOffSetTag;
            currentYOffSet = yAxisTag.getValue();
        }

        Map<String, Tag> copy = new HashMap<>(schematic); // We need to copy here
        int normalizedYOffset = (int) normalizeYaw(degree + currentYOffSet);
        copy.put("SCyAxisOffset", new IntTag(normalizedYOffset));

        CompoundTag tag = new CompoundTag(copy);

        try (NBTOutputStream outputStream = new NBTOutputStream(new GZIPOutputStream(new FileOutputStream(schematicFile)))) {
            outputStream.writeNamedTag("Schematic", tag);
        }

    }

    private static float normalizeYaw(float yaw) {
        float ya = yaw;
        if (yaw > 360) {
            int times = (int) ((ya - (ya % 360)) / 360);
            int normalizer = times * 360;
            ya -= normalizer;
        } else if (yaw < -360) {
            ya = Math.abs(ya);
            int times = (int) ((ya - (ya % 360)) / 360);
            int normalizer = times * 360;
            ya = yaw + normalizer;
        }
        return ya;
    }
    
    public int getBlockId(BlockVector pos) {
        return blockIds[index(pos)];
    }

    public int getBlockData(BlockVector pos) {
        return data[index(pos)];
    }

    public CompoundTag getTile(BlockVector pos) {
        Map<String, Tag> ctData = tileEntities.get(pos);
        return ctData != null ? new CompoundTag(ctData) : null;
    }

    @Override
    public short getBlockId(int x, int y, int z) {
        return blockIds[index(x, y, z)];
    }

    @Override
    public byte getBlockData(int x, int y, int z) {
        return data[index(x, y, z)];
    }

    public CompoundTag getTileEntity(int x, int y, int z) {
        return getTile(new BlockVector(x, y, z));
    }

    @Override
    public CompoundTag getTile(int x, int y, int z) {
        return getTile(new BlockVector(x, y, z));
    }

}
