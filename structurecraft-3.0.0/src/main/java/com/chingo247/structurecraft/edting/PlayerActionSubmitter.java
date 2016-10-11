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
package com.chingo247.structurecraft.edting;

import com.sk89q.worldedit.entity.Player;
import java.util.UUID;

/**
 *
 * @author Chingo247
 */
public class PlayerActionSubmitter implements ActionSubmitter {
    
    private Player player;

    public PlayerActionSubmitter(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
    
    @Override
    public void sendMessage(String message) {
        this.player.print(message);
    }

    @Override
    public void sendError(String message) {
        this.player.printError(message);
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }
    
}
