/*
 * Copyright (C) 2016 ching
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
package com.chingo247.structurecraft.events;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author ching
 */
public class PlayerSelectEvent {
    
    private Player player;
    private World world;
    private Vector pos1, pos2;

    public PlayerSelectEvent(Player player, World world, Vector pos1, Vector pos2) {
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Vector getPos1() {
        return pos1;
    }

    public Vector getPos2() {
        return pos2;
    }

    public World getWorld() {
        return world;
    }
    
    
    
}
