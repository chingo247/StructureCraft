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
package com.chingo247.structurecraft.editing.actions.selection;

import com.chingo247.structurecraft.editing.actions.Action;
import com.chingo247.structurecraft.events.plans.PlayerDeselectEvent;
import com.chingo247.structurecraft.services.event.EventDispatcher;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.entity.Player;

/**
 *
 * @author Chingo
 */
public class Deselect implements Action {
    
    private Player player;

    public Deselect(Player player) {
        Preconditions.checkNotNull(player, "player was null!");
        this.player = player;
    }

    @Override
    public void execute() {
        EventDispatcher.IMP.dispatchEvent(new PlayerDeselectEvent(player));
    }
    
    public Player getPlayer() {
        return player;
    }
    
    
    
    
}
