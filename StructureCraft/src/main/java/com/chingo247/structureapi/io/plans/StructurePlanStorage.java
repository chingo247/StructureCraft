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
package com.chingo247.structureapi.io.plans;

import com.chingo247.structureapi.model.plans.StructurePlan;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chingo
 */
public class StructurePlanStorage  {

    private final Map<Long, StructurePlan> plans;

    public StructurePlanStorage() {
        this.plans = new HashMap<>();
    }

    /**
     * Clears all plans from the storage. 
     */
    public void clear() {
        synchronized (plans) {
            plans.clear();
        }
    }
    
    
    

    /**
     * Gets a plan by it's hash
     *
     * @param planHash The hash
     * @return The plan matching the hash
     */
    public StructurePlan getPlan(long planHash) {
        synchronized (plans) {
            return plans.get(planHash);
        }
    }

    /**
     * Adds a StructurePlan to the storage
     *
     * @param plan The plan to add
     */
    public void add(StructurePlan plan) {
        synchronized (plans) {
            this.plans.put(plan.hash(), plan);
        }
    }

    /**
     * Gets all plans
     *
     * @return The plans
     */
    public Collection<StructurePlan> getPlans() {
        synchronized (plans) {
            return Collections.unmodifiableCollection(plans.values());
        }
    }

}
