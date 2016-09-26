/*
 * Copyright (C) 2015 Chingo
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

import com.chingo247.structurecraft.StructureCraft;
import com.chingo247.structurecraft.events.plans.PlayerDeselectEvent;
import com.chingo247.structurecraft.services.ServicesManager;
import com.chingo247.xplatform.core.IColors;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.UUID;

/**
 * A Non-selection manager. Which will print the current selection to the player
 * @author Chingo
 */
public class NoneSelectionManager extends ASelectionManager {
    
    private static NoneSelectionManager instance;
    private final IColors COLORS;
    
    private NoneSelectionManager() {
        this.COLORS = StructureCraft.IMP.getPlatform().getChatColors();
    }
    
    public static NoneSelectionManager getInstance() {
        if(instance == null) {
            instance = new NoneSelectionManager();
        }
        return instance;
    }

    @Override
    public void select(UUID player, Vector start, Vector end) {
        CuboidRegion dimension = new CuboidRegion(start, end);
        Vector min = dimension.getMinimumPoint();
        Vector max = dimension.getMaximumPoint();
        Player ply = ServicesManager.IMP.getWorldedit().getPlayer(player);
        
        ply.print(COLORS.white() + "You've selected ("+ 
                    COLORS.yellow() + "x:" + COLORS.white() + min.getBlockX() + " " +
                    COLORS.yellow() + "y:" + COLORS.white() + min.getBlockY() + " " +
                    COLORS.yellow() + "z:" + COLORS.white() + min.getBlockZ() + "),(" +
                    COLORS.yellow() + "x:" + COLORS.white() + max.getBlockX() + " " +
                    COLORS.yellow() + "y:" + COLORS.white() + max.getBlockY() + " " +
                    COLORS.yellow() + "z:" + COLORS.white() + max.getBlockY() + ")"
                + "\n" + COLORS.white() + "Click again to " + COLORS.green() + "confirm"
        );
        putSelection(new Selection(ply.getUniqueId(), start, end));
    }

    @Override
    public void deselect(UUID player) {
        if (!hasSelection(player)) return;
        removeSelection(getSelection(player));
    }

    @AllowConcurrentEvents
    @Subscribe
    public void onDeselect(PlayerDeselectEvent deselectEvent) {
        Player ply = deselectEvent.getPlayer();
        deselect(ply);
    }
    

    
    
}
