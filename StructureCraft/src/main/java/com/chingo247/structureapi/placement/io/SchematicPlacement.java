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
package com.chingo247.structureapi.placement.io;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.placement.BlockPlacement;
import com.chingo247.structureapi.placement.io.annotations.Resource;
import com.chingo247.structureapi.placement.io.annotations.XMLDeserializer;
import com.chingo247.structureapi.placement.io.annotations.XMLSerializer;
import com.chingo247.structureapi.placement.io.util.PlacementElement;
import com.chingo247.structureapi.plan.io.exception.PlanException;
import com.chingo247.structureapi.schematic.FastClipboard;
import com.chingo247.structureapi.schematic.Schematic;
import com.chingo247.structureapi.schematic.SchematicManager;
import com.chingo247.structureapi.util.WorldUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 */
public class SchematicPlacement extends BlockPlacement {

    public static final String TYPE = "schematic";
    
    private FastClipboard clipboard;
    private CuboidRegion placementRegion;
    private Schematic schematic;
    
    @Resource
    private File schematicFile;
    
    public SchematicPlacement(Schematic schematic) {
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

    @Override
    public BaseBlock getBlock(Vector position) {
        FastClipboard fastClipboard = getClipboard();
        return fastClipboard.getBlock(position);
    }
    
    private FastClipboard getClipboard() {
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
        return new BlockVector(fastClipboard.getWidth(), fastClipboard.getHeight(), fastClipboard.getLength()); //To change body of generated methods, choose Tools | Templates.
    }
    
    @XMLDeserializer(plugin = "structurecraft", type = TYPE)
    public static SchematicPlacement deserialize(XMLElement element) {
        PlacementElement root = new PlacementElement(element.getXmlFile(), element.getElement());
        String schematicPath = root.getSchematic();
        if (schematicPath == null) {
            throw new PlanException("Element '" + root.getElementName() + "' on line " + root.getLine() + " doesn't have an element called 'Schematic', error occured in '" + root.getFile().getAbsolutePath() + "'");
        }
        File schematicFile = new File(root.getFile().getParent(), schematicPath);
        if (!schematicFile.exists()) {
            throw new PlanException("Error in '" + root.getFile().getAbsolutePath() + "': File '" + schematicFile.getAbsolutePath() + "' defined in element '<Schematic>' does not exist!");
        }
        SchematicManager sdm = SchematicManager.getInstance();
        Schematic schematic = sdm.getOrLoadSchematic(schematicFile);
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
