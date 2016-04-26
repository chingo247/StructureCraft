/*
 * Copyright (C) 2015 Chingo
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
import java.io.File;
import java.io.IOException;

/**
 * StructurePlans contain next to placement information the meta data of a structure (e.g Name, Price, Category)
 * @author Chingo
 */
public abstract class StructurePlan<T extends IPlacementHolder> {
    
    private static final XXHasher HASHER = new XXHasher();
    private File file;
    private String name, category, description;
    private double price;
    private T holder;
    
    public StructurePlan(File file, String name, String category, double price, T holder) {
        this.file = file;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public T getHolder() {
        return holder;
    }
    
    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Saves this structure plan (and related files) to designated directory
     * @param directory The directory to export to
     */
    public abstract void export(File directory);
    
    /**
     * Computes the hash value for PATH of the file planFile of this StructurePlan
     * @return The hash
     * @throws IOException 
     */
    public long hash() throws IOException {
        return HASHER.hash64String(file.getAbsolutePath());
    }
   
}
