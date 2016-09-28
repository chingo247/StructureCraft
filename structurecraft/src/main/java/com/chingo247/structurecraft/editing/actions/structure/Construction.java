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
import com.chingo247.structurecraft.editing.actions.PlayerAction;
import com.chingo247.structurecraft.editing.block.BlockSession;
import com.chingo247.structurecraft.editing.context.BlockPlaceContext;
import com.chingo247.structurecraft.model.structure.Structure;
import com.sk89q.worldedit.entity.Player;
import java.util.UUID;

/**
 *
 * @author Chingo247
 */
public abstract class Construction implements Cancellable, PlayerAction {
    
    private static final UUID CONSOLE = UUID.randomUUID();
    
    protected final BlockSession session;
    private final Structure structure;
    private final BlockPlaceContext context;
    
    public Construction(Structure structure, BlockPlaceContext context) {
        this.structure = structure;
        this.context = context;
        this.session = context.getSessionFactory().createSession(context.getWorld().getName(), context.getPlayer() == null ? CONSOLE : context.getPlayer().getUniqueId());
    }
    
    public Structure getStructure() {
        return structure;
    }
    
    public BlockPlaceContext getContext() {
        return context;
    }
    
    @Override
    public Player getPlayer() {
        return context.getPlayer();
    }

    public BlockSession getSession() {
        return session;
    }

    @Override
    public void cancel() {
        session.cancel();
    }

}
