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
package com.chingo247.structurecraft.editing.actions.plan;

import com.chingo247.structurecraft.StructureCraft;
import com.chingo247.structurecraft.StructureCraftConfig;
import com.chingo247.structurecraft.economy.IEconomy;
import com.chingo247.structurecraft.editing.Scheduler;
import com.chingo247.structurecraft.editing.actions.Action;
import com.chingo247.structurecraft.edting.selection.CUISelector;
import com.chingo247.structurecraft.editing.actions.structure.StructureCreator;
import com.chingo247.structurecraft.editing.items.StructurePlanItem;
import static com.chingo247.structurecraft.editing.items.StructurePlanItem.isStructurePlan;
import com.chingo247.structurecraft.edting.session.PlayerSession;
import com.chingo247.structurecraft.io.plans.StructurePlanStorage;
import com.chingo247.structurecraft.model.OwnerType;
import com.chingo247.structurecraft.model.placement.IPlacement;
import com.chingo247.structurecraft.model.plans.StructurePlan;
import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.model.world.Direction;
import com.chingo247.structurecraft.persistence.dao.StructureDAO;
import com.chingo247.structurecraft.services.ServicesManager;
import com.chingo247.structurecraft.services.permission.PermissionManager;
import com.chingo247.structurecraft.util.PlacementUtil;
import com.chingo247.structurecraft.util.WorldUtil;
import com.chingo247.structurecraft.xplatform.core.IColors;
import com.chingo247.structurecraft.xplatform.core.IPlayer;
import com.chingo247.structurecraft.xplatform.core.item.IInventory;
import com.chingo247.structurecraft.xplatform.core.item.IItemStack;
import com.chingo247.structurecraft.xplatform.core.server.APlatform;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author Chingo
 */
public class PlacePlan implements Action {

    private PlayerSession session;
//    private PlacePlanContext context;
    private IItemStack item;
    private Vector clickedPos;

    public PlacePlan(PlayerSession session, IItemStack planItem, Vector clickedPos) {
        Preconditions.checkArgument(StructurePlanItem.isStructurePlan(item), "Item is not a plan");
        
        this.clickedPos = clickedPos;
        this.session = session;
    }

    @Override
    public void execute() {
            // Check if this Itemstack is a StructurePlan
            if (!isStructurePlan(item)) {
                return;
            }
            final IPlayer player = StructureCraft.IMP.getPlatform().getPlayer(session.getPlayer().getUniqueId());

            // Check if the player has permission to structures
            APlatform pf = StructureCraft.IMP.getPlatform();
            IColors color = pf.getChatColors();

            if (!PermissionManager.getInstance().isAllowed(player, PermissionManager.Perms.STRUCTURE_PLACE)) {
                player.sendMessage(color.red() + "You have no permission to place structures");
                return;
            }

            // Pick a selection type
            Player wePlayer = ServicesManager.IMP.getWorldedit().getPlayer(player.getUniqueId());
            

            // Dispatch a async task for structure creation at this players queue
            Scheduler.IMP.scheduleForPlayer(player.getUniqueId(), () -> {
                try {
                    // Inventory check
                    IInventory inventory = player.getInventory();
                    if (!inventory.hasItem(item)) {
                        return;
                    }

                    String planId = StructurePlanItem.getPlanID(item);

                    StructurePlanStorage planStorage = StructureCraft.IMP.getStructurePlansStorage();
                    StructurePlan plan = planStorage.getPlan(Long.parseLong(planId));

                    // If the plan is null... and this is a plan... REFUND if possible
                    if (plan == null) {
                        if (StructureCraft.IMP.isLoadingPlans()) {
                            player.sendMessage(color.red() + "Plans are not loaded yet... please wait...");
                            return;
                        }

                        player.sendMessage(color.red() + "The plan has become invalid, reason: data was not found");
                        int amount = item.getAmount();
                        double value = StructurePlanItem.getPrice(item);
                        IEconomy economyProvider = ServicesManager.IMP.getEconomy();

                        if (economyProvider != null && value > 0.0d) {
                            economyProvider.give(player.getUniqueId(), value * amount);
                            player.sendMessage(color.red() + "Invalid StructurePlans have been removed and you've been refunded: " + (value * amount));
                        } else {
                            player.sendMessage(color.red() + "Removed invalid structure plans from your inventory");
                        }
                        player.getInventory().removeItem(item);

                        // Otherwise, plan is not null... soo place it!
                    } else {

                        Direction direction = WorldUtil.getDirection(player.getYaw());
                        Vector pos1 = clickedPos;
                        Vector pos2;
                        boolean toLeft = player.isSneaking();

                        IPlacement placement = plan.getPlacementSource().load();
                        CuboidRegion selection = placement.getCuboidRegion();

                        if (toLeft) {
                            pos2 = PlacementUtil.getPoint2Left(pos1, direction, selection.getMaximumPoint());
                        } else {
                            pos2 = PlacementUtil.getPoint2Right(pos1, direction, selection.getMaximumPoint());
                        }

                        if (!session.hasSelection()) {
                            session.select(pos1, pos2);
                            if (session.getSelector() instanceof CUISelector) {
                                player.sendMessage(color.yellow() + "Left-Click " + color.reset() + " in the " + color.green() + " green " + color.reset() + "square to " + color.yellow() + "confirm");
                                player.sendMessage(color.yellow() + "Right-Click " + color.reset() + "to" + color.yellow() + " deselect");
                            }
                        } else if (session.matchesCurrentSelection(pos1, pos2)) { // Means second time so place!

                            // To left means the player was sneaking
                            // This is a shortcut for placing the structure at the left of him
                            if (toLeft) {
                                // Fix WTF HOW?!!1?
                                pos1 = WorldUtil.translateLocation(pos1, direction, (-(selection.getMaximumPoint().getBlockZ() - 1)), 0, 0);
                            }

                            StructureCraftConfig config = StructureCraft.IMP.getConfig();

                            Structure parent = null;
                            DBI dbi = ServicesManager.IMP.getDBI();
                            try (Handle h = dbi.open()) {
                                StructureDAO structureDAO = h.attach(StructureDAO.class);
                                parent = structureDAO.findStructureOnPosition(player.getWorld().getUUID(), pos1);
                            }

                            Structure structure = new StructureCreator()
                                    .setPlan(plan)
                                    .setRefundValue(plan.getPrice())
                                    .addOwner(player.getUniqueId(), OwnerType.MASTER)
                                    .create(wePlayer, plan.getName(), wePlayer.getWorld().getName(), pos1, direction);
                                    
                            if (structure != null) {
                                // remove item from inventory
                                IItemStack clone = item.cloneItem();
                                clone.setAmount(1);
                                player.getInventory().removeItem(clone);
                                player.updateInventory();
                            } 
                            
                            session.deselect();
                        } else {
                            session.deselect();
                            session.select(pos1, pos2);
                            if (session.getSelector() instanceof CUISelector) {
                                player.sendMessage(color.yellow() + "Left-Click " + color.reset() + " in the " + color.green() + " green " + color.reset() + "square to " + color.yellow() + "confirm");
                                player.sendMessage(color.yellow() + "Right-Click " + color.reset() + "to" + color.yellow() + " deselect");
                            }
                        }

                    }
                } catch (Exception ex) { // Log the exception HERE, otherwise it will appear nowhere
                    player.sendMessage(color.red() + "[SettlerCraft-StructureAPI]: An error occured... see console");
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            });

    }

 
}
