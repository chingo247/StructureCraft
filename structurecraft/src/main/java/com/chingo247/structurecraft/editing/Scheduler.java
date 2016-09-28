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
package com.chingo247.structurecraft.editing;

import com.chingo247.structurecraft.util.concurrent.KeyPool;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Monitor;
import com.sk89q.worldedit.world.World;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

/**
 * This class is used to schedule tasks and critical actions over worlds. This class uses the {@link ForkJoinPool#commonPool() } as executor, 
 * which initially has a parallelism of: amount of processors - 1.
 * 
 * 
 * @author Chingo247
 */
public class Scheduler {

    public static final Scheduler IMP = new Scheduler();
    
    private static final Logger LOG = Logger.getLogger(Scheduler.class.getName());
    private final Map<String, Monitor> monitors;
    private KeyPool<String> executor;
    private KeyPool<UUID> playerPool;

    private Scheduler() { 
        this.monitors = Maps.newHashMap();
        this.executor = new KeyPool<>(ForkJoinPool.commonPool());
        this.playerPool = new KeyPool<>(ForkJoinPool.commonPool());
    }
    
    public void scheduleForPlayer(UUID player, Runnable task) {
        this.playerPool.execute(player, task);
    }
    
    /**
     * Gets the monitor for a certain world
     * @param key The world
     * @return The monitor
     */
    public @Nonnull Monitor getMonitor(String key) {
        synchronized(monitors) {
            Monitor m = monitors.get(key);
            if (m == null) {
                m = new Monitor();
                this.monitors.put(key, m);
            }
            return m;
        }
    }
    
    /**
     * Executes a world critical task. All tasks scheduled with this method will wait for the monitor
     * to be able to enter
     * @param key The key
     * @param runnable The runnable
     */
    public void executeCritical(String key, Runnable runnable) {
        Monitor monitor = getMonitor(key);
        monitor.enter();
        try {
            runnable.run();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            monitor.leave();
        }
    }
    
    /**
     * Executes a world critical task. All tasks scheduled with this method will wait for the monitor
     * to be able to enter
     * @param world The world
     * @param runnable The runnable
     */
    public void executeCritical(World world, Runnable runnable) {
        Monitor monitor = getMonitor(world.getName());
        monitor.enter();
        try {
            runnable.run();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            monitor.leave();
        }
    }
    
    /**
     * Queues a task, doesn't wait for any locks or monitors. Just waits in the queue... until it's his turn to be executed
     * @param world The world
     * @param runnable The task to execute
     */
    public void queue(String world, Runnable runnable) {
        this.executor.execute(world, runnable);
    } 
            
    
}
