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
package com.chingo247.structurecraft.editing.actions.structure;

import com.chingo247.structurecraft.editing.actions.Create;
import com.chingo247.structurecraft.editing.context.PlaceContext;
import com.chingo247.structurecraft.editing.context.CreateStructureContext;
import com.chingo247.structurecraft.model.structure.ConstructionStatus;
import com.chingo247.structurecraft.model.structure.Structure;
import com.chingo247.structurecraft.model.world.Spatial;
import com.chingo247.structurecraft.persistence.repositories.StructureRepository;
import com.chingo247.structurecraft.editing.restrictions.Restriction;
import com.chingo247.structurecraft.editing.Scheduler;
import com.chingo247.structurecraft.editing.restrictions.Violation;
import com.chingo247.structurecraft.services.ServicesManager;
import com.chingo247.structurecraft.util.PlacementUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.ArrayList;
import java.util.List;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

/**
 *
 * @author Chingo247
 */
public class CreateStructure implements Create<Structure> {
    
    private List<Restriction<PlaceContext>> restrictions;
    private CreateStructureContext context;
    private Structure structure;
    
    public CreateStructure(CreateStructureContext context) {
        this.context = context;
    }
    
    public void addRestriction(Restriction<PlaceContext> restriction) {
        if (restrictions == null) restrictions = new ArrayList<>();
        
        this.restrictions.add(restriction);
    }

    public void setRestrictions(List<Restriction<PlaceContext>> restrictions) {
        this.restrictions = restrictions;
    }
    
    @Override
    public synchronized Structure create() {
        final Vector point2 = PlacementUtil.getPoint2Right(context.getPosition(), context.getDirection(), context.getPosition().add(context.getPlacement().getCuboidRegion().getMaximumPoint()));
        final CuboidRegion affected = new CuboidRegion(context.getPosition(), point2);
        
        Scheduler.IMP.executeCritical(context.getWorld().getName(), () -> {
            PlaceContext placeContext = new PlaceContext(context.getWorld(), context.getPlayer(), affected);
            
            for (Restriction<PlaceContext> restriction : restrictions) {
                Violation violation = restriction.checkViolation(placeContext);
                if (violation != null) {
                    context.printError(violation.getMessage());
                    return;
                }
            }
            
            
                DBI dbi = ServicesManager.IMP.getDBI();
                try (Handle handle = dbi.open()) {
                    Handle tx = handle.begin();
                    try {
                        
                        StructureRepository sr = new StructureRepository(dbi, handle);
                        Vector min = affected.getMinimumPoint(), max = affected.getMaximumPoint();
                      
                        
                        Spatial spatial = new Spatial(min.getBlockX(), max.getBlockX(), min.getBlockY(), max.getBlockY(), min.getBlockZ(), max.getBlockZ(), context.getWorldUUID(), context.getWorld().getName(), context.getDirection().getDirectionId());
                        structure = new Structure(spatial, context.getName());
                        structure.setRefundValue(context.getRefundValue());
                        structure.setStatus(ConstructionStatus.ON_HOLD);
                        long id = sr.add(structure);
                        structure.setId(id);
                        
                        
                        
                        tx.commit();
                    } catch(Exception ex) {
                        structure = null;
                        tx.rollback();
                    } finally {
                        tx.close();
                    }
                }
          
        });
        return structure;
    }
}
