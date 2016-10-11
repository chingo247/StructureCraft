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
package com.chingo247.structurecraft;

import com.chingo247.structurecraft.services.ServiceAlreadyRegisteredException;
import com.chingo247.structurecraft.services.IPermissionRegistry;
import com.chingo247.structurecraft.io.schematic.SchematicStorage;
import com.chingo247.structurecraft.edting.selection.CUISelector;
import com.chingo247.structurecraft.edting.selection.NoneSelectionManager;
import com.chingo247.structurecraft.edting.block.BlockSessionFactory;
import com.chingo247.structurecraft.editing.context.PlaceContext;
import com.chingo247.structurecraft.editing.restrictions.Restriction;
import com.chingo247.structurecraft.exception.StructureCraftException;
import com.chingo247.structurecraft.events.StructurePlansLoadedEvent;
import com.chingo247.structurecraft.events.StructurePlansReloadEvent;
import com.chingo247.structurecraft.io.plans.StructurePlanReader;
import com.chingo247.structurecraft.model.plans.StructurePlan;
import com.chingo247.structurecraft.io.plans.StructurePlanStorage;
import com.chingo247.structurecraft.services.*;
import com.chingo247.structurecraft.services.config.ConfigProvider;
import com.chingo247.structurecraft.services.event.DefaultSubscriberExceptionHandler;
import com.chingo247.structurecraft.services.event.EventDispatcher;
import com.chingo247.structurecraft.services.permission.Permission;
import com.chingo247.structurecraft.services.permission.PermissionDefault;
import com.chingo247.structurecraft.util.XXHasher;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import java.io.File;
import java.util.ArrayList;
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

    private final AsyncEventBus asyncEventBus;
    private final EventBus eventBus;

    // Concurrency
    private ExecutorService executor;
    private final Lock plansReloadLock;

    // StructureCraft
    private final StructurePlanStorage planStorage;
    private IPermissionRegistry permissionRegistry;
    private APlatform platform;
    private SchematicStorage schematicStorage;
    private List<Restriction<PlaceContext>> restrictions;

    //TODO in CONFIG
    private static int CACHED_OBJECTS = 50;

    /**
     * Constructor.
     */
    private StructureCraft() {
        this.executor = ForkJoinPool.commonPool();
        this.asyncEventBus = new AsyncEventBus(executor, new DefaultSubscriberExceptionHandler());
        this.eventBus = new EventBus(new DefaultSubscriberExceptionHandler());
        this.planStorage = new StructurePlanStorage();
        this.plansReloadLock = new ReentrantLock();

        EventDispatcher.IMP.register(eventBus);
        EventDispatcher.IMP.register(asyncEventBus);

        asyncEventBus.register(CUISelector.getInstance());
        asyncEventBus.register(NoneSelectionManager.getInstance());

    }

    /**
     * Registers the StructureCraft plugin.
     *
     * @param plugin The StructureCraft plugin
     * @throws StructureCraftException if already registered a plugin
     */
    public void registerStructureCraft(IPlugin plugin) throws StructureCraftException, ServiceAlreadyRegisteredException {
        if (this.plugin != null) {
            throw new StructureCraftException("Already registered a plugin");
        }
        this.plugin = plugin;
        initialize();
    }

    private void initialize() throws ServiceAlreadyRegisteredException {
        Preconditions.checkArgument(plugin != null, "StructureCraft was not yet registered!"); // Sanity check
        File datadir = new File(plugin.getDataFolder(), "data");
        datadir.mkdirs();
        this.schematicStorage = new SchematicStorage(new File(datadir, "schematics"), CACHED_OBJECTS);
    }

    public SchematicStorage getSchematicStorage() {
        return schematicStorage;
    }

    /**
     * Registers the server platform
     *
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
     *
     * @return The directory of StructureCraft
     */
    public File getStructureCraftDirectory() {
        File directory = plugin.getDataFolder();
        directory.mkdirs();
        return plugin.getDataFolder();
    }

    /**
     * Gets the platform
     *
     * @return
     */
    public APlatform getPlatform() {
        return platform;
    }

    /**
     * The StructurePlans directory
     *
     * @return The directory
     */
    public File getPlansDirectory() {
        File pluginDirectory = plugin.getDataFolder();
        return new File(pluginDirectory, "plans");
    }

    /**
     * The StructurePlan Storage that is used by StructureCraft
     *
     * @return The Plan Storage
     */
    public StructurePlanStorage getStructurePlansStorage() {
        return planStorage;
    }

    /**
     * Reloads the structure plans of StructureCraft
     *
     * @return False, if there is already a reload running
     */
    public boolean reloadPlans() {
        if (plansReloadLock.tryLock()) {
            try {
                EventDispatcher.IMP.dispatchEvent(new StructurePlansReloadEvent());
                planStorage.clear();

                //TODO: Configureable load in config?
                executor.submit(() -> {
                    try {
                        // First bulk import the schematics...
                        schematicStorage.importSchematics(getPlansDirectory(), true);

                        // Read the structure plans and insert them in the storage
                        StructurePlanReader reader = new StructurePlanReader();
                        List<StructurePlan> plansList = reader.readDirectory(getPlansDirectory());
                        for (StructurePlan plan : plansList) {
                            planStorage.add(plan);
                        }

                        // Set the permissive structureplans
                        setPlanPermissions(new File(getPlansDirectory(), "restricted"), "structurecraft.plans", true);

                        EventDispatcher.IMP.dispatchEvent(new StructurePlansLoadedEvent());
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
                if (f.isDirectory()) {
                    setPlanPermissions(f, f.getName(), false);
                }
            }
        } else {

            for (File f : directory.listFiles()) {
                if (f.isDirectory()) {
                    setPlanPermissions(f, permission + "." + f.getName(), false);
                } else {
                    StructurePlan plan = planStorage.getPlan(HASHER.hash64String(f.getAbsolutePath()));
                    if (plan != null) {
                        plan.setPermission(permission);
                    }
                }
            }
        }
    }

    /**
     * Gets the AsyncEventbus used by StructureCraft
     *
     * @return The AsyncEventbus
     */
    public AsyncEventBus getAsyncEventBus() {
        return asyncEventBus;
    }

    /**
     * Gets the Eventbus used by StructureCraft
     *
     * @return The eventbus
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    public boolean isLoadingPlans() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ConfigProvider getConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BlockSessionFactory getSessionFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addRestriction(Restriction<PlaceContext> restriction) {
        if (restrictions == null) {
            restrictions = new ArrayList<>();
        }

        this.restrictions.add(restriction);
    }

    public List<Restriction<PlaceContext>> getRestrictions() {
        return restrictions;
    }
    
    

}
