/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.placement;

import com.chingo247.structureapi.session.BlockPlaceSession;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public interface IPlaceable {
    
    
    void place(BlockPlaceSession session, Vector position);
    
    
}
