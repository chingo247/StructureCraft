/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.services;

import com.chingo247.structurecraft.services.permission.IPermissionRegistry;
import com.chingo247.structurecraft.economy.IEconomy;
import com.chingo247.structurecraft.persistence.connection.IDBIProvider;
import com.chingo247.structurecraft.services.permission.PermissionManager;
import com.google.common.base.Preconditions;
import org.skife.jdbi.v2.DBI;

/**
 *
 * @author Chingo
 */
public class ServicesManager {
    
    public static final ServicesManager IMP = new ServicesManager();
    
    private IEconomy economy;
    private IWorldEditIntegration worldedit;
    private IDBIProvider dBIProvider;

    private ServicesManager() {
    }

    public void setEconomy(IEconomy economy) {
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

    public IEconomy getEconomy() {
        return economy;
    }

    public IWorldEditIntegration getWorldedit() {
        return worldedit;
    }
    
}
