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
package com.chingo247.structureapi;

import com.chingo247.settlercraft.core.concurrent.ThreadPoolFactory;
import com.chingo247.settlercraft.core.event.DefaultSubscriberExceptionHandler;
import com.chingo247.settlercraft.core.event.EventDispatcher;
import com.chingo247.settlercraft.core.platforms.services.IPermissionRegistry;
import com.chingo247.settlercraft.core.platforms.services.permission.Permission;
import com.chingo247.settlercraft.core.platforms.services.permission.PermissionDefault;
import com.chingo247.settlercraft.core.util.XXHasher;
import com.chingo247.structureapi.events.plans.StructurePlansLoadedEvent;
import com.chingo247.structureapi.events.plans.StructurePlansReloadEvent;
import com.chingo247.structureapi.model.persistent.structure.Structure;
import com.chingo247.structureapi.io.plans.StructurePlanReader;
import com.chingo247.structureapi.model.plans.StructurePlan;
import com.chingo247.structureapi.io.plans.StructurePlanStorage;
import com.chingo247.structureapi.io.schematic.SchematicStorage;
import com.chingo247.structureapi.platform.permission.PermissionManager;
import com.chingo247.structureapi.platform.permission.PermissionManager.Perms;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class StructureCraft {

    public static final StructureCraft IMP = new StructureCraft();
    
    
    private static final Logger LOG = Logger.getLogger(StructureCraft.class.getName());
    private static final XXHasher HASHER = new XXHasher();
    
    private IPlugin plugin;
    
    // Event stuff
    private final EventDispatcher eventDispatcher;
    private final AsyncEventBus asyncEventBus;
    private final EventBus eventBus;
    
    // Concurrency
    private ExecutorService executor;
    private final Lock plansReloadLock;
    
    // StructureCraft
    private final StructurePlanStorage planStorage;
    private IPermissionRegistry permissionRegistry;
    private APlatform platform;
    
    /**
     * Constructor.
     */
    private StructureCraft() {
        this.eventDispatcher = new EventDispatcher();
        this.asyncEventBus = new AsyncEventBus(getExecutor(), new DefaultSubscriberExceptionHandler());
        this.eventBus = new EventBus(new DefaultSubscriberExceptionHandler());
        this.planStorage = new StructurePlanStorage();
        this.plansReloadLock = new ReentrantLock();
    }

    /**
     * Registers the StructureCraft plugin.
     * @param plugin The StructureCraft plugin
     * @throws StructureCraftException if already registered a plugin
     */
    public void registerStructureCraft(IPlugin plugin) throws StructureCraftException {
        if (this.plugin != null) {
            throw new StructureCraftException("Already registered a plugin");
        }
        this.plugin = plugin;
    }
    
    /**
     * Register a IPermissionRegistry. 
     * @param permissionRegistry The IPermissionRegistry to register
     * @throws StructureCraftException if already registered a IPermissionRegistry
     */
    public void registerPermissionRegistry(IPermissionRegistry permissionRegistry) throws StructureCraftException {
        if (this.permissionRegistry != null) {
            throw new StructureCraftException("Already registered a permissionRegistry");
        }
        this.permissionRegistry = permissionRegistry;
        
        for (Perms p : PermissionManager.Perms.values()) {
            permissionRegistry.registerPermission(p.getPermission());
        }
    }
    
    /**
     * Registers the server platform
     * @param platform The server platform
     * @throws StructureCraftException If already registerd a platform
     */
    public void registerPlatform(APlatform platform) throws StructureCraftException {
        if (this.platform != null) {
            throw new StructureCraftException("Already registered a platform");
        }
        this.platform = platform;
    }

    /**
     * Gets the plugin directory of StructureCraft.
     * @return The directory of StructureCraft
     */
    public File getStructureCraftDirectory() {
        File directory = plugin.getDataFolder();
        directory.mkdirs();
        return plugin.getDataFolder();
    }

    /**
     * Gets the directory of a structure
     * @param structure The structure
     * @return The directory of the structure
     */
    public File getDirectoryForStructure(Structure structure) {
        File worldDirector = new File(getStructureCraftDirectory(), "worlds/" + structure.getWorldName() + "/");
        File structuresDir =  new File(worldDirector, "structures/" + structure.getId() + "/");
        structuresDir.mkdirs();
        return structuresDir;
    }

    /**
     * Gets the platform
     * @return 
     */
    public APlatform getPlatform() {
        return platform;
    }
    
    /**
     * The StructurePlans directory
     * @return The directory
     */
    public File getPlansDirectory() {
        File pluginDirectory = plugin.getDataFolder();
        return new File(pluginDirectory, "plans");
    }

    /**
     * The StructurePlan Storage that is used by StructureCraft
     * @return The Plan Storage
     */
    public StructurePlanStorage getPlanStorage() {
        return planStorage;
    }

    /**
     * Reloads the structure plans of StructureCraft
     * @return False, if there is already a reload running
     */
    public boolean reloadPlans() {
        if (plansReloadLock.tryLock()) {
            try {
                eventDispatcher.dispatchEvent(new StructurePlansReloadEvent());
                planStorage.clear();
                
                //TODO: Configureable load in config?
                
                executor.submit(() -> {
                    try {
                        StructurePlanReader reader = new StructurePlanReader();
                        SchematicStorage.IMP.loadSchematicsFromDirectory(getPlansDirectory());
                        List<StructurePlan> plansList = reader.readDirectory(getPlansDirectory());
                        for (StructurePlan plan : plansList) {
                            planStorage.add(plan);
                        }
                        
                        setPlanPermissions(new File(getPlansDirectory(), "restricted"), "structurecraft.plans", true);
                        
                        
                        eventDispatcher.dispatchEvent(new StructurePlansLoadedEvent());
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex); 
                    }
                });
            } finally {
                plansReloadLock.unlock();
            }
            return true;
        }
        return false;
    }
    
    private void setPlanPermissions(File directory, String permission, boolean isRoot) {
        
        permissionRegistry.registerPermission(new Permission(permission, PermissionDefault.FALSE, "Permission for StructurePlans"));
        
        if (isRoot) {
            for (File f : directory.listFiles()) {
                if(f.isDirectory()) {
                    setPlanPermissions(f, f.getName(), false);
                }
            }
        } else {
            
            for (File f : directory.listFiles()) {
                if(f.isDirectory()) {
                    setPlanPermissions(f, permission + "." + f.getName(), false);
                } else {
                    StructurePlan plan = planStorage.getPlan(HASHER.hash64String(f.getAbsolutePath()));
                    if(plan != null) {
                        plan.setPermission(permission);
                    }
                }
            }
        }
    }
    
    
    /**
     * Gets the AsyncEventbus used by StructureCraft
     * @return The AsyncEventbus
     */
    public AsyncEventBus getAsyncEventBus() {
        return asyncEventBus;
    }
    
    /**
     * Gets the Eventbus used by StructureCraft
     * @return The eventbus
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Gets the EventDispatcher which will dispatch events to all eventbusses 
     * that are registered to it including the ones used by StructureCraft
     * @return The EventDispatcher
     */
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    /**
     * Gets the ExecutorService that is used by StructureCraft to create threads
     * @return The ExecutorService
     */
    public final ExecutorService getExecutor() {
        if(executor == null) {
            executor = new ThreadPoolFactory().newCachedThreadPool(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors());
        }
        
        return executor;
    }
    
}
