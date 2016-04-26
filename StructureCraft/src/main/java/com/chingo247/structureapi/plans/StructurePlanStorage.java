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

import com.chingo247.settlercraft.core.util.XXHasher;
import com.chingo247.structureapi.plan.StructurePlanException;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * StructurePlan
 *
 * @author Chingo
 */
public class StructurePlanStorage {

    private static final XXHasher HASHER = new XXHasher();
    private final StructurePlanRepository planRepository;
    private final Map<Long, IStructurePlanHolder> plans;
    private final GraphDatabaseService graph;
    private ExecutorService executor;

    public StructurePlanStorage(GraphDatabaseService graph) {
        this.plans = Maps.newHashMap();
        this.planRepository = new StructurePlanRepository(graph);
        this.graph = graph;
    }

    public void addPlan(IStructurePlanHolder planHolder) throws IOException, StructurePlanException {
        StructurePlan plan = planHolder.load();
        long hash = plan.hash();
        try (Transaction tx = graph.beginTx()) {
            plans.put(plan.hash(), planHolder);
            planRepository.addPlan(plan.getName(), plan.getCategory(), plan.getDescription(), plan.getPrice(), hash);
            tx.success();
        }
    }

    public void addPlans(Iterable<IStructurePlanHolder> plansToAdd) throws IOException, StructurePlanException {
        try (Transaction tx = graph.beginTx()) {
            for (IStructurePlanHolder planHolder : plansToAdd) {
                StructurePlan plan = planHolder.load();
                long hash = plan.hash();

                plans.put(plan.hash(), planHolder);
                planRepository.addPlan(plan.getName(), plan.getCategory(), plan.getDescription(), plan.getPrice(), hash);
            }
            
            
            tx.success();
        }
    }
    
    public void clearAll() {
        try (Transaction tx = graph.beginTx()) {
            planRepository.deleteAll();
            plans.clear();
            tx.success();
        }
    }

    public boolean isLoaded(File planFile) throws IOException {
        long hash = HASHER.hash64(planFile);
        return plans.containsKey(hash);
    }

}
