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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;

/**
 *
 * @author Chingo
 */
public class StructurePlanRepository {

    private GraphDatabaseService graph;

    public StructurePlanRepository(GraphDatabaseService graph) {
        this.graph = graph;
    }

    public StructurePlanNode addPlan(String name, String category, String description, double price, long hash) throws IOException {
        Node node = graph.createNode(DynamicLabel.label(StructurePlanNode.LABEL));
        StructurePlanNode planNode = new StructurePlanNode(node);
        planNode.setHash(hash);
        planNode.setName(name);
        planNode.setCategory(category);
        planNode.setDescription(description);
        planNode.setPrice(price);
        return planNode;
    }
    
    public Collection<StructurePlanNode> findAll() {
        return findAll(-1, -1);
    }

    public Collection<StructurePlanNode> findAll(long skip, long limit) {
        Map<String, Object> params = Maps.newHashMap();
        if (skip > 0) {
            params.put("skip", skip);
        }
        if (limit > 0) {
            params.put("limit", limit);
        }

        String query = "MATCH (sp:" + StructurePlanNode.LABEL + ") ";
        if (skip > 0) {
            query += "SKIP {skip}";
        }
        if (limit > 0) {
            query += "LIMIT {limit}";
        }
        query += " RETURN sp ORDER BY sp." + StructurePlanNode.NAME_PROPERTY + " ASC";

        Result r = graph.execute(query, params);
        List<StructurePlanNode> list = Lists.newArrayList();
        while (r.hasNext()) {
            Map<String, Object> rMap = r.next();
            list.add(new StructurePlanNode((Node) rMap.get("sp")));
        }
        return list;
    }
    
    public Collection<StructurePlanNode> findByCategory(String category, long skip, long limit) {
        Map<String, Object> params = Maps.newHashMap();
        if (skip > 0) {
            params.put("skip", skip);
        }
        if (limit > 0) {
            params.put("limit", limit);
        }
        params.put("category", category);

        String query = "MATCH (sp:" + StructurePlanNode.LABEL + " { "+StructurePlanNode.CATEGORY_PROPERTY+": {category} }) ";
        if (skip > 0) {
            query += "SKIP {skip}";
        }
        if (limit > 0) {
            query += "LIMIT {limit}";
        }
        query += " RETURN sp ORDER BY sp." + StructurePlanNode.NAME_PROPERTY + " ASC";

        Result r = graph.execute(query, params);
        List<StructurePlanNode> list = Lists.newArrayList();
        while (r.hasNext()) {
            Map<String, Object> rMap = r.next();
            list.add(new StructurePlanNode((Node) rMap.get("sp")));
        }
        return list;
    }
    
    public void deleteAll() {
        String query = "MATCH (sp:"+StructurePlanNode.LABEL+")-[r]-() "
                + "DELETE sp, r";
        graph.execute(query);
        
    }
}
