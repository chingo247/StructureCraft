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

import com.chingo247.structurecraft.editing.actions.PlayerAction;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;

/**
 *
 * @author Chingo
 */
public class Select implements PlayerAction {
    
    private ISelectionManager selectionManager;
    private Player player;
    private Vector pos1, pos2;

    public Select(ISelectionManager selectionManager, Player player, Vector pos1, Vector pos2) {
        Preconditions.checkNotNull(selectionManager, "selectionManager was null!");
        Preconditions.checkNotNull(player, "player was null!");
        
        this.selectionManager = selectionManager;
        this.player = player;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public void execute() {
        selectionManager.select(player, pos1, pos2);
    }
    
    public Player getPlayer() {
        return player;
    }
    
    
    
    
}
