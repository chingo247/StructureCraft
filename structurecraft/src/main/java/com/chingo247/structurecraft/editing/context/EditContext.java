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
package com.chingo247.structurecraft.editing.context;

import com.chingo247.structurecraft.StructureCraft;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class EditContext extends Context {
    
    private final UUID worldUUID;
    private final World world;
    private final Player player;

    public EditContext(World world, Player editor) {
        Preconditions.checkNotNull(world, "World may not be null");
        
        IWorld w = StructureCraft.IMP.getPlatform().getServer().getWorld(world.getName());
        this.worldUUID = w.getUUID();
        this.world = world;
        this.player = editor;
    }
    
    public UUID getWorldUUID() {
        return worldUUID;
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }
    
    public void print(String msg) {
        if (player == null) {
            APlatform platform = StructureCraft.IMP.getPlatform();
            platform.getConsole().sendMessage(msg);
        } else {
            player.print(msg);
        }
    }
    
    public void printError(String msg) {
        if (player == null) {
            APlatform platform = StructureCraft.IMP.getPlatform();
            IColors colors = platform.getChatColors();
            platform.getConsole().sendMessage(colors.red() + msg);
        } else {
            player.printError(msg);
        }
    }
    

}
