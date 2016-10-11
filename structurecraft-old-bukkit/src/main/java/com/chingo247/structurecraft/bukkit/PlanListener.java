package com.chingo247.structurecraft.bukkit;

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
import com.chingo247.structurecraft.editing.actions.plan.PlacePlan;
import com.chingo247.structurecraft.editing.items.StructurePlanItem;
import com.chingo247.xplatform.core.AItemStack;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlatform;
import com.chingo247.structurecraft.services.ServicesManager;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.entity.Player;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Chingo
 */
public class PlanListener implements Listener {

    public PlanListener() {
    }

    @EventHandler
    public void onPlayerUsePlan(final PlayerInteractEvent pie) {
        
        if (pie.getItem() == null) {
            return;
        }
        
        AItemStack stack = BukkitPlatform.wrapItem(pie.getItem());
        if (StructurePlanItem.isStructurePlan(stack)) {
            pie.setCancelled(true);
        }

        UUID playerUUID = pie.getPlayer().getUniqueId();
        Block block = pie.getClickedBlock();
        boolean clickedAir = (block == null || pie.getAction().equals(Action.RIGHT_CLICK_AIR) || pie.getAction().equals(Action.RIGHT_CLICK_BLOCK));
        Location location = block!= null ? block.getLocation() : null;
        Player player = ServicesManager.IMP.getWorldedit().getPlayer(playerUUID);
        PlayerSession session = new PlayerSession(player);

        if(location == null || clickedAir) {
            session.deselect();
        } else {
            PlacePlan placePlan = new PlacePlan(session, stack, new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            placePlan.execute();
        }

    }

}
