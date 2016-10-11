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

import com.chingo247.structurecraft.editing.actions.Cancellable;
import com.chingo247.structurecraft.edting.block.BlockSession;
import com.chingo247.structurecraft.edting.session.Session;
import com.chingo247.structurecraft.model.structure.Structure;

/**
 *
 * @author Chingo247
 */
public abstract class Construction implements Cancellable {
    
    protected final BlockSession blockSession;
    private final Structure structure;
    private final Session session;
    
    public Construction(Structure structure, BlockSession blockSession, Session session) {
        this.structure = structure;
        this.session = session;
        this.blockSession = blockSession;
    }
    
    public Structure getStructure() {
        return structure;
    }
       
    public BlockSession getBlockSession() {
        return blockSession;
    }

    @Override
    public void cancel() {
        blockSession.cancel();
    }

}
