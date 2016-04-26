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

import com.chingo247.structureapi.placement.io.XMLElement;
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
 *
 * @author Chingo
 */
public class XMLStructurePlan extends StructurePlan<XMLPlacementHolder> {

    public XMLStructurePlan(File file, String name, String category, double price, XMLPlacementHolder holder) {
        super(file, name, category, price, holder);
    }

    @Override
    public void export(File directory) {
        XMLPlacementHolder holder = getHolder();
        XMLElement xmlElement = holder.getPlacementElement();

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
            Logger.getLogger(XMLStructurePlan.class.getName()).log(Level.SEVERE, null, ex);
            for(File f : written) {
                f.delete(); // cleanup
            }
        }

    }

}
