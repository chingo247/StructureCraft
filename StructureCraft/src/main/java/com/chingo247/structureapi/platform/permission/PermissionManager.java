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
package com.chingo247.structureapi.platform.permission;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.platforms.services.IPermissionRegistry;
import com.chingo247.settlercraft.core.platforms.services.permission.Permission;
import com.chingo247.settlercraft.core.platforms.services.permission.PermissionDefault;
import com.chingo247.xplatform.core.IPlayer;
import com.sk89q.worldedit.entity.Player;
import java.util.UUID;
import com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 */
public class PermissionManager {
    
    private static final String PREFIX = "settlercraft.";
    private static PermissionManager instance;

    private PermissionManager() {
    }
    
    public static PermissionManager getInstance() {
        if(instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }
    
    public enum Perms {
        CONTENT_RELOAD_PLANS(new Permission(Permissions.RELOAD_PLANS, PermissionDefault.OP, "Allows a player to reload plans on the server")),
        CONTENT_ROTATE_PLACEMENT(new Permission(Permissions.ROTATE_SCHEMATIC, PermissionDefault.OP, "Allows a player to rotate the original schematic for all future placing")),
        
        SETTLER_GIVE_PLAN(new Permission(Permissions.GIVE_PLAN, PermissionDefault.OP, "Give a single plan to another player")),
        SETTLER_ME(new Permission(Permissions.SETTLER_ME, PermissionDefault.TRUE, "Give a single plan to another player")),
        SETTLER_OPEN_PLAN_MENU(new Permission(Permissions.OPEN_PLANMENU, PermissionDefault.OP, "Allows a player to use the plan menu (contains plans for FREE)")),
        
        SETTLER_OPEN_SHOP_MENU(new Permission(Permissions.OPEN_PLANSHOP, PermissionDefault.TRUE, "Allows a player to use the plan shop")),
        
        STRUCTURE_CREATE(new Permission(Permissions.STRUCTURE_CREATE, PermissionDefault.OP, "Allows a player to create structures from selections")),
        STRUCTURE_PLACE(new Permission(Permissions.STRUCTURE_PLACE, PermissionDefault.TRUE, "Allows the player to place structures")),
        STRUCTURE_DEMOLISH(new Permission(Permissions.STRUCTURE_DEMOLISH, PermissionDefault.TRUE, "Allows the player to demolish a structure")),
        STRUCTURE_BUILD(new Permission(Permissions.STRUCTURE_BUILD, PermissionDefault.TRUE, "Allows the player to build a structure")),
        STRUCTURE_HALT(new Permission(Permissions.STRUCTURE_HALT, PermissionDefault.TRUE, "Allows the player to halt any construction process of a structure")),
        STRUCTURE_ROLLBACK(new Permission(Permissions.STRUCTURE_ROLLBACK, PermissionDefault.TRUE, "Allows the player to rollback a structure")),
        STRUCTURE_INFO(new Permission(Permissions.STRUCTURE_INFO, PermissionDefault.TRUE, "Allows the player to show information about a structure")),
        STRUCTURE_LIST(new Permission(Permissions.STRUCTURE_LIST, PermissionDefault.TRUE, "Allows the player to show a list structures he or another player owns")),
        STRUCTURE_LOCATION(new Permission(Permissions.STRUCTURE_LOCATION, PermissionDefault.TRUE, "Allows the player to show his relative position to a structure")),
        
        SETTLER_CONSTRUCTION_ZONE_CREATE(new Permission(Permissions.CONSTRUCTIONZONE_CREATE, PermissionDefault.OP, "Allows a player to create construction zones")),
        SETTLER_CONSTRUCTION_ZONE_DELETE(new Permission(Permissions.CONSTRUCTIONZONE_DELETE, PermissionDefault.OP, "Allows a player to delete construction zones"));
        private Permission permission;

        private Perms(Permission permission) {
            this.permission = permission;
        }

        public Permission getPermission() {
            return permission;
        }
        
    }
    
    public boolean isAllowed(Player player, Perms permission) {
        return isAllowed(player.getUniqueId(), permission);
    }
    
    public boolean isAllowed(UUID player, Perms permission) {
        return isAllowed(SettlerCraft.getInstance().getPlatform().getPlayer(player), permission);
    }

    public boolean isAllowed(IPlayer player, Perms permission) {
        if(player == null) return false;
        if(player.isOP() && permission.permission.getDefault() != PermissionDefault.FALSE) return true;
        return player.hasPermission(permission.permission.getName());
    }
    
    
}
