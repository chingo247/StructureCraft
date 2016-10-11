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
package com.chingo247.structurecraft.services;

import com.chingo247.structurecraft.services.connection.IDBIProvider;
import com.chingo247.structurecraft.services.permission.PermissionManager;
import com.google.common.base.Preconditions;
import org.skife.jdbi.v2.DBI;

/**
 * This class holds several services like economy hooks, database connections, permission registers, etc. 
 * @author Chingo
 */
public class ServicesManager {
    
    public static final ServicesManager IMP = new ServicesManager();
    
    private IEconomyProvider economy;
    private IWorldEditIntegration worldedit;
    private IDBIProvider dBIProvider;

    private ServicesManager() {
    }

    public void setEconomy(IEconomyProvider economy) {
        this.economy = economy;
    }
    
    public void registerWorldEdit(IWorldEditIntegration worldEdit) {
        Preconditions.checkState(this.worldedit == null, "WorldEdit already registered!");
        this.worldedit = worldEdit;
    }
    
    public void registerDBIProvider(IDBIProvider dBIProvider) {
        Preconditions.checkState(this.dBIProvider == null, "DBI already registered!");
        this.dBIProvider = dBIProvider;
    }
    
    public void registerPermissions(IPermissionRegistry registry) {
        for (PermissionManager.Perms p : PermissionManager.Perms.values()) {
            registry.registerPermission(p.getPermission());
        }
    }

    public DBI getDBI() {
        return dBIProvider.getDBI();
    }
    
    public IDBIProvider getDBIProvider() {
        return dBIProvider;
    }

    public IEconomyProvider getEconomy() {
        return economy;
    }

    public IWorldEditIntegration getWorldedit() {
        return worldedit;
    }

    
}
