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
package com.chingo247.structurecraft.io.xml;

import com.chingo247.structurecraft.io.util.PlacementElement;
import com.chingo247.structurecraft.util.XXHasher;
import com.google.common.base.Preconditions;
import fiber.core.impl.BuildContextImpl;
import fiber.core.impl.xml.ModelReader;
import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import fiber.core.impl.xml.located.LocatedElement;

/**
 *
 * @author Chingo
 */
public class StructurePlanDocument extends LineElement {

    private final File file;
    private final Document doc;

    StructurePlanDocument(File f, Document d) {
        super(f, (LocatedElement) d.getRootElement());
        Preconditions.checkNotNull(f);
        Preconditions.checkNotNull(d);

        this.file = f;
        this.doc = d;
    }

    public boolean hasSubStructureElements() {
        return le.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_SUBSTRUCTURES) != null;
    }

    public String getReferenceId() {
        Node idNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_ID_ELEMENT);
        if (idNode == null) {
            String path = file.getAbsolutePath();
            return String.valueOf(new XXHasher().hash32String(path));
        }
        LineElement element = new LineElement(getFile(), (LocatedElement) idNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }

    public String getDescription() {
        Node descNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_DESCRIPTION_ELEMENT);
        if (descNode == null) {
            return "None";
        }
        LineElement element = new LineElement(getFile(), (LocatedElement) descNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }

    public String getName() {
        Node nameNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_NAME_ELEMENT);
        if (nameNode == null) {
            return FilenameUtils.getBaseName(file.getName());
        }
        LineElement element = new LineElement(getFile(), (LocatedElement) nameNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }

    public double getPrice() {
        Node priceNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_PRICE_ELEMENT);
        if (priceNode == null) {
            return 0.0;
        }
        LineElement priceElement = new LineElement(getFile(), (LocatedElement) priceNode);
        return priceElement.getDoubleValue();
    }

    public PlacementElement getPlacementElement() {
        checkNotNull(StructurePlanXMLConstants.STRUCTURE_PLAN_PLACEMENT);
        Node n = le.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_PLACEMENT);
        return new PlacementElement(getFile(), (LocatedElement) n);
    }

    public static StructurePlanDocument read(File xmlFile) throws DocumentException {
        ModelReader reader = ModelReader.getNonValidatingInstance(new BuildContextImpl());
        Document d = reader.read(xmlFile);
        return new StructurePlanDocument(xmlFile, d);
    }

    public String getCategory() {
        Node categoryNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_CATEGORY_ELEMENT);
        if (categoryNode == null) {
            return null;
        }
        LineElement element = new LineElement(getFile(), (LocatedElement) categoryNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }

}
