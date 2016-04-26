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

import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class StructurePlanNode {

    public static final String LABEL = "STRUCTUREPLAN";
    public static final String NAME_PROPERTY = "name";
    public static final String CATEGORY_PROPERTY = "category";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String PRICE_PROPERTY = "price";
    
    private Node underlyingNode;
    
    

    public StructurePlanNode(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getUnderlyingNode() {
        return underlyingNode;
    }

    public String getName() {
        return (String) underlyingNode.getProperty("name");
    }
    
    public void setName(String name) {
        underlyingNode.setProperty("name", name);
    }

    public String getCategory() {
        return (String) underlyingNode.getProperty("category", "default");
    }
    
    public void setCategory(String category) {
        underlyingNode.setProperty("category", category);
    }

    public String getDescription() {
        return (String) underlyingNode.getProperty("description", "");
    }
    
    public void setDescription(String description) {
        underlyingNode.setProperty("description", description);
    }
    
    public double getPrice() {
        return (double) underlyingNode.getProperty("price", 0);
    }
    
    public void setPrice(double price) {
        underlyingNode.setProperty("price", price);
    }
    
    public void setHash(long hash) {
        underlyingNode.setProperty("hash", hash);
    }
    
    public long getHash() {
        return (long) underlyingNode.getProperty("hash");
    }

}
