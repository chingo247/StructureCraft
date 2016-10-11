/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.structurecraft.xplatform.bukkit;

import com.chingo247.structurecraft.xplatform.core.IColors;
import com.chingo247.structurecraft.xplatform.core.ICommandSender;
import com.chingo247.structurecraft.xplatform.core.item.IInventory;
import com.chingo247.structurecraft.xplatform.core.item.IItemStack;
import com.chingo247.structurecraft.xplatform.core.server.APlatform;
import com.chingo247.structurecraft.xplatform.core.server.IServer;
import com.google.common.base.Preconditions;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class BukkitPlatform extends APlatform {
    
    private final BukkitServer bukkitServer;
    
    public BukkitPlatform(Server server) {
        this(new BukkitServer(server));
    }

    public BukkitPlatform(BukkitServer bukkitServer) {
        Preconditions.checkNotNull(bukkitServer, "Server was null");
        this.bukkitServer = bukkitServer;
    }

    @Override
    public IServer getServer() {
        return bukkitServer;
    }

    @Override
    public ICommandSender getConsole() {
        return bukkitServer.getConsole();
    }

    @Override
    public File getPluginsFolder() {
        return new File("plugins");
    }

    @Override
    public IItemStack createItemStack(int material) {
        return new BukkitItemStack(new ItemStack(material));
    }
    
    @Override
    public IColors getChatColors() {
        return BukkitChatColors.IMP;
    }

    @Override
    public IInventory createInventory(String title, int slots) {
        return new BukkitInventory(Bukkit.createInventory(null, slots, title));
    }
    
    public static IItemStack wrapItem(ItemStack stack) {
        if(stack != null) {
            return new BukkitItemStack(stack);
        } 
        return null;
    }

    

   
    
}
