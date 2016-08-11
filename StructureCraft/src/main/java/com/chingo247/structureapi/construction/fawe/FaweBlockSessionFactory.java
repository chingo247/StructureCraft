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
package com.chingo247.structureapi.construction.fawe;

import com.boydti.fawe.FaweAPI;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.construction.block.BlockSession;
import com.chingo247.structureapi.construction.block.BlockSessionFactory;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class FaweBlockSessionFactory extends BlockSessionFactory {
    
    private EditSessionFactory sessionFactory;

    public FaweBlockSessionFactory(EditSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    
    
    @Override
    public BlockSession createSession(String world, UUID player) {
        World w = SettlerCraft.getInstance().getWorld(world);
        Player p = SettlerCraft.getInstance().getPlayer(player);
        EditSession session = player == null ? sessionFactory.getEditSession(w, -1) : sessionFactory.getEditSession(w, -1, p);
        return new FaweBlockSession(FaweAPI.createQueue(world, true), session, p);
    }
    
}
