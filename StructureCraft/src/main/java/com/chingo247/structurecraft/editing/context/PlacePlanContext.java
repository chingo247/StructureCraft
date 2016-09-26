/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.editing.context;

import com.chingo247.structurecraft.editing.actions.selection.ISelectionManager;
import com.chingo247.structurecraft.services.ServicesManager;
import com.chingo247.xplatform.core.AItemStack;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class PlacePlanContext extends EditContext {
    
    private final boolean clickedAir;
    private final AItemStack planItem;
    private final Vector position;

    public PlacePlanContext(Player player, World world, Vector position, boolean clickedAir, AItemStack planItem) {
        super(world, player);
        Preconditions.checkNotNull(player, "player was null");
        Preconditions.checkNotNull(world, "world was null");
        Preconditions.checkNotNull(planItem, "planItem was null");
        
        this.clickedAir = clickedAir;
        this.planItem = planItem;
        this.position = position;
    }
    
    public Vector getPosition() {
        return position;
    }

    public AItemStack getPlanItem() {
        return planItem;
    }

    public boolean isClickedAir() {
        return clickedAir;
    }
    
}
