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
package com.chingo247.structureapi.plans;

import com.chingo247.structureapi.placement.IPlacement;
import com.chingo247.structureapi.placement.exception.PlacementException;
import com.chingo247.structureapi.placement.exception.PlacementNotRegisteredException;

/**
 * Used to hold a placement so it can be requested on demand
 * @author Chingo
 */
public interface IPlacementHolder {
    
    /**
     * Gets the plugin that can load this placement
     * @return The plugin
     */
    String getPlugin();
    
    /**
     * Gets the type name of the placement
     * @return The type name
     */
    String getType();
    
    /**
     * Loads the placement
     * @return The 
     * @throws PlacementNotRegisteredException if the placement's loader was not registered yet
     */
    IPlacement load() throws PlacementException;
    
    /**
     * Checks if the placement of this holder can be loaded
     * @return True if load is possible
     */
    boolean canLoad();
    
}
