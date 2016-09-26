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
package com.chingo247.structurecraft.io.plans;

import com.chingo247.structurecraft.model.plans.StructurePlan;
import com.chingo247.structurecraft.io.plans.predicate.IStructurePlanPredicate;
import com.chingo247.xplatform.core.IPlayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Chingo
 */
public class StructurePlansProvider implements IStructurePlansProvider {

    private StructurePlanStorage storage;
    private List<IStructurePlanPredicate> predicates;

    public StructurePlansProvider(StructurePlanStorage storage) {
        this.storage = storage;
        this.predicates = new ArrayList<>();
    }

    public void addPredicate(IStructurePlanPredicate predicate) {
        this.predicates.add(predicate);
    }
    
    public Iterable<IStructurePlanPredicate> getPredicates() {
        return Collections.unmodifiableList(predicates);
    }

    @Override
    public Collection<StructurePlan> getPlans(IPlayer player) {
        return storage.getPlans()
                .stream()
                .parallel()
                .filter(p -> filter(p, player))
                .collect(Collectors.toList());
    }
    
    private boolean filter(StructurePlan plan, IPlayer player) {
        for(IStructurePlanPredicate predicate : predicates) {
            if(!predicate.evaluate(plan, player)) {
                return false;
            }
        }
        return true;
    }

}
