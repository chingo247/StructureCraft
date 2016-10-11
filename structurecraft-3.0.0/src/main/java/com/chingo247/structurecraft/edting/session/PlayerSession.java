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
package com.chingo247.structurecraft.edting.session;

import com.chingo247.structurecraft.edting.PlayerActionSubmitter;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.chingo247.structurecraft.edting.selection.CUISelector;
import com.chingo247.structurecraft.edting.selection.ISelector;
import com.chingo247.structurecraft.edting.selection.NoneSelectionManager;

/**
 *
 * @author Chingo
 */
public class PlayerSession extends Session {
    
    private final Player player;
    private ISelector selectionManager;
    
    /**
     * Constructor.
     * @param actionSubmitter The player action submitter
     */
    public PlayerSession(Player player) {
//        super(new PlayerActionSubmitter(player));
        
        this.player = player;
        LocalSession session = WorldEdit.getInstance().getSessionManager().get(player);
        this.selectionManager = session.hasCUISupport() ? CUISelector.getInstance() : NoneSelectionManager.getInstance();
    }

    /**
     * Gets the selector
     * @return The selector
     */
    public ISelector getSelector() {
        return selectionManager;
    }
    
    
    /**
     * Gets the player holding the session
     * @return The player
     */
    public final Player getPlayer() {
        return player;
    }
    
    /**
     * Creates a selection
     * @param pos1 The first pos of the selection
     * @param pos2 The second pos of the selectio
     */
    public void select(Vector pos1, Vector pos2) {
        this.selectionManager.select(player, pos1, pos2);
    }
    
    /**
     * Checks if the current selection of the player matches the given selection
     * @param pos1 The first pos of the selection
     * @param pos2 The second pos of the selection
     * @return 
     */
    public boolean matchesCurrentSelection(Vector pos1, Vector pos2) {
        return selectionManager.matchesCurrentSelection(player, pos1, pos2);
    }
    
    /**
     * Checks if the player has an active selection
     * @return True if the player has an active selection
     */
    public boolean hasSelection() {
        return this.selectionManager.hasSelection(player);
    }
    
    /**
     * Clears the current selection of the player
     */
    public void deselect() {
        this.selectionManager.deselect(player);
    }
    
    /**
     * Sets the selector (see {@link ISelector}). This will first clear the current selection
     * @param selector 
     */
    public void setSelector(ISelector selector) {
        Preconditions.checkNotNull(selector, "SelectionManager may not be null!");
        this.selectionManager.deselect(player);
        this.selectionManager = selector;
    }
    
}
