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
package com.chingo247.menuapi.menu;


import com.chingo247.structurecraft.economy.IEconomy;
import com.chingo247.structurecraft.xplatform.core.IColors;
import com.chingo247.structurecraft.xplatform.core.item.IItemStack;
import com.chingo247.structurecraft.xplatform.core.server.APlatform;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class MenuAPI {
    
    private final Map<UUID, ACategoryMenu> openMenus;
    private APlatform platform;
    private final IColors COLORS;
    private IEconomy economy;
   
    private static MenuAPI instance;
    
    private MenuAPI() {
        this.openMenus = Maps.newHashMap();
        this.COLORS = platform.getChatColors();
    }
    
    
    public static MenuAPI getInstance() {
        if(instance == null) {
            instance = new MenuAPI();
        }
        return instance;
    }
    
    
    public APlatform getPlatform() {
        return platform;
    }
   
    public void registerEconomy(IEconomy economy) {
        if (this.economy != null) {
            throw new RuntimeException("Already registered an economy");
        }
    }
    
    public void registerPlatform(APlatform platform) {
        if (this.platform != null) {
            throw new RuntimeException("Already registered a platform");
        }
        this.platform = platform;
    }
    
    void registerMenu(ACategoryMenu menu) {
        synchronized(openMenus) {
            this.openMenus.put(menu.getPlayer().getUniqueId(), menu);
        }
    }
    
    public final ACategoryMenu getMenu(UUID player) {
        synchronized(openMenus) {
            return openMenus.get(player);
        }
    }
    
    private void unRegisterMenu(UUID player) {
        synchronized(openMenus) {
            openMenus.remove(player);
        }
    }

    
    
    public final void onPlayerLeave(UUID player) {
        unRegisterMenu(player);
    }
    
    public final void onServerReload() {
        for(Iterator<ACategoryMenu> it = openMenus.values().iterator(); it.hasNext();) {
            ACategoryMenu menu = it.next();
            if(menu != null) {
                menu.close(COLORS.yellow() + "Closing menus, server is reloading...");
                it.remove();
            }
        }
    }
    
    public void closeMenusWithTag(String tag, String reason) {
        for(Iterator<ACategoryMenu> it = openMenus.values().iterator(); it.hasNext();) {
            ACategoryMenu menu = it.next();
            if(menu != null && menu.getTag() != null && tag.equals(menu.getTag())) {
                menu.close(COLORS.yellow() +"Closing menus, reason: " + reason);
                it.remove();
            }
        }
    }
    
    public final boolean onPlayerClick(int slot, UUID player, int clickType, IItemStack clicked, IItemStack itemOnCursor, IEconomy economy) {
        if(clicked == null) return false;
        
        ACategoryMenu menu = openMenus.get(player);
        if(menu != null) {
            
            
            return menu.onMenuSlotClicked(slot, clickType, clicked, itemOnCursor, economy);
        } else {
            return false;
        }
    }
    
}
