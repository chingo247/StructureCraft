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
package com.chingo247.structurecraft.editing.actions.structure;

import com.chingo247.structurecraft.edting.block.BlockSession;
import com.chingo247.structurecraft.editing.context.BlockPlaceContext;
import com.chingo247.structurecraft.model.structure.Structure;

/**
 *
 * @author Chingo247
 */
public class Demolish extends Construction {

    public Demolish(Structure structure, BlockPlaceContext context, BlockSession session) {
        super(structure, context, session);
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
