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
package com.chingo247.structurecraft.editing.actions;

import com.chingo247.structurecraft.StructureCraft;
import com.chingo247.structurecraft.xplatform.core.IWorld;
import com.sk89q.worldedit.world.World;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public abstract class AbstractEdit implements Edit {
    
    private UUID player;
    private UUID worldUUID;
    private World world;

    public AbstractEdit(UUID player, World world) {
        this.player = player;
        IWorld w = StructureCraft.IMP.getPlatform().getServer().getWorld(world.getName());
        this.world = world;
        this.worldUUID = w.getUUID();
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public UUID getPlayer() {
        return player;
    }

    @Override
    public UUID getWorldUUID() {
        return worldUUID;
    }
    
    
    
    
    
}
