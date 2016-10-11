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
package com.chingo247.structurecraft.model.placement;

import com.chingo247.structurecraft.StructureCraft;
import com.chingo247.structurecraft.exception.StructurePlanException;
import com.chingo247.structurecraft.io.placement.XMLElement;
import com.chingo247.structurecraft.io.annotations.Resource;
import com.chingo247.structurecraft.io.annotations.XMLDeserializer;
import com.chingo247.structurecraft.io.annotations.XMLSerializer;
import com.chingo247.structurecraft.io.util.PlacementElement;
import com.chingo247.structurecraft.model.container.FastClipboard;
import com.chingo247.structurecraft.io.schematic.ISchematic;
import com.chingo247.structurecraft.io.schematic.Schematic;
import com.chingo247.structurecraft.model.world.Direction;
import com.chingo247.structurecraft.io.schematic.SchematicStorage;
import com.chingo247.structurecraft.util.WorldUtil;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import org.dom4j.Node;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 */
public class SchematicPlacement extends CuboidPlacement {

    public static final String TYPE = "schematic";
    
    private FastClipboard clipboard;
    private final CuboidRegion placementRegion;
    private final ISchematic schematic;
    
    @Resource(name = "Schematic")
    private File schematicFile;
    
    public SchematicPlacement(ISchematic schematic) {
        super(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
        
        Direction currentDirection = WorldUtil.getDirection(getRotation());
        this.rotate(schematic.getAxisOffset());
        Direction newDirection = WorldUtil.getDirection(getRotation());
       
        int schematicWidth = schematic.getWidth();
        int schematicLength = schematic.getLength();
        int schematicHeight = schematic.getHeight();
        
        if(((currentDirection == Direction.EAST || currentDirection == Direction.WEST) && (newDirection == Direction.NORTH || newDirection == Direction.SOUTH))
                || ((currentDirection == Direction.NORTH || currentDirection == Direction.SOUTH) && (newDirection == Direction.WEST || newDirection == Direction.EAST))) {
            int temp = schematic.getWidth();
            schematicWidth = schematic.getLength();
            schematicLength = temp;
        }
      
        this.placementRegion = new CuboidRegion(Vector.ZERO, new BlockVector(schematicWidth, schematicHeight, schematicLength));
        this.schematic = schematic;
        this.schematicFile = schematic.getFile();
    }

    public BaseBlock getBlock(BlockVector position) {
        FastClipboard fastClipboard = getClipboard();
        return fastClipboard.getBlock(position);
    }
    
    private FastClipboard getClipboard() { // Lazy load
        if(clipboard == null) {
            this.clipboard = schematic.getClipboard(); // Expensive! loads the clipboard
        }
        return clipboard;
    }
    
    @Override
    public CuboidRegion getRegion() {
        return placementRegion;
    }
    
    @Override
    public Vector getSize() {
        FastClipboard fastClipboard = getClipboard();
        return new BlockVector(fastClipboard.getWidth(), fastClipboard.getHeight(), fastClipboard.getLength()); 
    }
    
        @Override
    public short getBlockId(int x, int y, int z) {
        return clipboard.getBlockId(x, y, z);
    }

    @Override
    public byte getBlockData(int x, int y, int z) {
        return clipboard.getBlockData(x, y, z);
    }

    public @Nullable@Override
        CompoundTag getTile(int x, int y, int z) {
        return clipboard.getTileEntity(x, y, z);
    }

    @Override
    protected BaseBlock getBlock(int blockX, int blockY, int blockZ) {
        return clipboard.getBlock(blockX, blockY, blockZ);
    }

    @Override
    public boolean hasBlock(int x, int y, int z) {
        return clipboard.hasBlock(x, y, z);
    }
    
    @XMLDeserializer(plugin = "structurecraft", type = TYPE)
    public static SchematicPlacement deserialize(XMLElement element) throws StructurePlanException, IOException {
        PlacementElement root = new PlacementElement(element.getXmlFile(), element.getElement());
        String schematicPath = root.getSchematic();
        if (schematicPath == null) {
            throw new StructurePlanException("Element '" + root.getElementName() + "' on line " + root.getLine() + " doesn't have an element called 'Schematic', error occured in '" + root.getFile().getAbsolutePath() + "'");
        }
        File schematicFile = new File(root.getFile().getParent(), schematicPath);
        if (!schematicFile.exists()) {
            throw new StructurePlanException("Error in '" + root.getFile().getAbsolutePath() + "': File '" + schematicFile.getAbsolutePath() + "' defined in element '<Schematic>' does not exist!");
        }
        
        SchematicStorage schematicStorage = StructureCraft.IMP.getSchematicStorage();
        ISchematic schematic = schematicStorage.getSchematic(Schematic.hash(schematicFile));
        return new SchematicPlacement(schematic);
    }
    
    @XMLSerializer(plugin = "structurecraft", type = TYPE)
    public static void serialize(SchematicPlacement placement, XMLElement root) {
        // NOTE: Type is already set! based on annotation
        Node schematicNode = new BaseElement("schematic");
        schematicNode.setText(placement.schematic.getFile().getName());
        root.getElement().add(schematicNode);
    }


    
}
