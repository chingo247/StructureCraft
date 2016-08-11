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
package com.chingo247.structureapi.construction.fawe;

import com.chingo247.structureapi.io.placement.PlacementSource;
import com.chingo247.structureapi.model.placement.IPlacement;
import com.chingo247.structureapi.util.task.ManualTask;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo247
 */
public class FawePlaceTask extends ManualTask<Long> {
    
    private IPlacement placement;
    private PlacementSource placementSource;
    private FaweBlockSession session;
    private Vector position;

    public FawePlaceTask(Long id, PlacementSource source, Vector position, FaweBlockSession session) {
        super(id);
        
        this.placementSource = source;
    }
    
    

    public FawePlaceTask(Long id, IPlacement placement, Vector position, FaweBlockSession session) {
        super(id);
        
        this.placement = placement;
    }

    @Override
    protected void task() throws Exception {
        if (placement == null) {
            placement = placementSource.load();
        }
        
        
        placement.place(session, position);
        onCancel(() -> { session.cancel(); });
        session.onComplete(() -> {
            FawePlaceTask.this.succes();
        });

        session.commit();   
    }

    
    
    
    
}
