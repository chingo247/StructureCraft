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
package com.chingo247.structureapi.construction;

import com.chingo247.structureapi.StructureCraft;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 *
 * @author Chingo247
 */
public class PlayerManager {
    
    public static final PlayerManager IMP = new PlayerManager();
    public static final PlayerEntry CONSOLE = new PlayerEntry(UUID.randomUUID(), (message) -> {System.out.println(message);});
    
    private Map<UUID,PlayerEntry> players;

    private PlayerManager() {
        this.players = Maps.newHashMap();
    }
    
    public @Nonnull PlayerEntry getEntry(UUID playerId) {
        final APlatform platform = StructureCraft.IMP.getPlatform();
        final IPlayer player = platform.getPlayer(playerId);
        PlayerEntry entry;
        if (player != null) {
            entry = players.get(playerId);
            if (entry == null) entry = new PlayerEntry(playerId, (message) -> {player.sendMessage(message);});
        } else {
            entry = CONSOLE;
        }
        
        return entry;
    }
    
    
}
