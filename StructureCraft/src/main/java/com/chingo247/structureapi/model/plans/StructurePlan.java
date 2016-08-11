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
package com.chingo247.structureapi.model.plans;

import com.chingo247.settlercraft.core.util.XXHasher;
import com.chingo247.structureapi.io.placement.XMLElement;
import com.chingo247.structureapi.io.placement.PlacementSource;
import com.chingo247.structureapi.util.XMLUtils;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Element;

/**
 * StructurePlans contain next to placement information the meta data of a
 * structure (e.g Name, Price, Category)
 *
 * @author Chingo
 */
public class StructurePlan {

    private static final XXHasher HASHER = new XXHasher();
    private File file;
    private String name, category, description;
    private double price;
    private PlacementSource placementLoader;
    private String permission;

    /**
     * Constructor
     * @param file The file of the plan
     * @param name The name of the plan
     * @param category The category this plan belongs to 
     * @param price The price of the plan
     * @param placmentSource The {@link PlacementSource} that is attached to this plan
     */
    public StructurePlan(File file, String name, String category, double price, PlacementSource placmentSource) {
        this.file = file;
        this.name = name;
        this.category = category;
        this.price = price;
        this.placementLoader = placmentSource;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public String getPermission() {
        return permission;
    }

    /**
     * Gets the StructurePlan file
     * @return The file
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets the name of the plan
     * @return The name of the plan
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the plan
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the category of the plan
     * @return The category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the plan
     * @param category The category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the description of the plan
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the plan
     * @param description The description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the price of the plan
     * @return The price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the plan
     * @param price 
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the placement source. So it can be loaded by the caller of this method
     * @return The placementsource
     */
    public PlacementSource getPlacementSource() {
        return placementLoader;
    }
    
    
    /**
     * Saves this structure plan (and related files) to designated directory
     *
     * @param directory The directory to export to
     */
    public void export(File directory) {
        XMLElement xmlElement = placementLoader.getPlacementElement();
        File xmlFile = getFile();
        Set<File> written = Sets.newHashSet();
        try {
            File destinationFile = new File("structureplan.xml");
            Files.copy(xmlFile.toPath(), destinationFile.toPath());
            written.add(destinationFile);
            Element root = xmlElement.getElement();
            List<Element> fileElements = XMLUtils.findElementsWithAttribute(root, "file");
            for (Element fileElement : fileElements) {
                String path = fileElement.getText();
                File resource = new File(xmlFile.getParent(), path);
                File copyTo = new File(directory, path);
                Files.copy(resource.toPath(), copyTo.toPath());
                written.add(copyTo);
            }
        } catch (IOException ex) {
            Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
            for (File f : written) {
                f.delete(); // cleanup
            }
        }

    }

    /**
     * Computes the hash value for PATH of the file planFile of this
     * StructurePlan
     * @return The hash
     */
    public long hash() {
        return HASHER.hash64String(file.getAbsolutePath());
    }
  

}
