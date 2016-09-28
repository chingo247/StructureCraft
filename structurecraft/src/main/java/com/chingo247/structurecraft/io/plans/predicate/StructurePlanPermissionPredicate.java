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
package com.chingo247.structurecraft.io.plans.predicate;

import com.chingo247.structurecraft.model.plans.StructurePlan;
import com.chingo247.xplatform.core.IPlayer;
import java.io.StringWriter;

/**
 *
 * @author Chingo
 */
public class StructurePlanPermissionPredicate implements IStructurePlanPredicate{

    @Override
    public boolean evaluate(StructurePlan plan, IPlayer player) {
        String permission = plan.getPermission();
        
        if (permission == null) {
            return true;
        }
        
        String[] perms = permission.split("\\.");
        StringWriter sw = new StringWriter();
        
        for (String perm : perms) {
            sw.write(perm);
            
            if (player.hasPermission(sw.toString())) {
                return true;
            }
            sw.write(".");
        }
        return false;
    }
    
}
