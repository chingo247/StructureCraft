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
package com.chingo247.structureapi.io.placement;

import com.chingo247.structureapi.model.placement.IPlacement;
import com.chingo247.structureapi.exception.placement.PlacementNotRegisteredException;
import com.chingo247.structureapi.io.placement.PlacementRegister;
import com.chingo247.structureapi.io.placement.XMLElement;

/**
 *
 * @author Chingo
 */
public class PlacementSource  {
    
    private XMLElement placementElement;
    private String type, plugin;

    public PlacementSource(XMLElement placementElement, String type, String plugin) {
        this.placementElement = placementElement;
        this.type = type;
        this.plugin = plugin;
    }

    public XMLElement getPlacementElement() {
        return placementElement;
    }
    
    public IPlacement load() throws PlacementNotRegisteredException {
        return PlacementRegister.instance().deserialize(placementElement);
    }

    public boolean canLoad() {
        return PlacementRegister.instance().isRegistered(plugin, type);
    }
    
    
}
