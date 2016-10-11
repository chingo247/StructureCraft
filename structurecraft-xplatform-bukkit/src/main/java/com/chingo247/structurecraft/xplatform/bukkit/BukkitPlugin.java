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


import com.chingo247.structurecraft.xplatform.core.server.APlatform;
import com.chingo247.structurecraft.xplatform.core.server.IPlugin;
import com.chingo247.structurecraft.xplatform.core.server.IScheduler;
import com.google.common.base.Preconditions;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class BukkitPlugin implements IPlugin {
    
    private final Plugin plugin;

    public BukkitPlugin(Plugin plugin) {
        Preconditions.checkNotNull(plugin);
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }
    
    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public IScheduler getScheduler() {
        return new BukkitScheduler(Bukkit.getServer(), plugin);
    }

    @Override
    public APlatform getPlatform() {
        return new BukkitPlatform(Bukkit.getServer());
    }
    
    
    
}
