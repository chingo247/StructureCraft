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
package com.chingo247.structurecraft.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Element;

/**
 *
 * @author Chingo
 */
public class XMLUtils {
    
    private XMLUtils(){}
    
    public static List<Element> findElementsWithAttribute(Element element, String attributeName) {
        return findElementsWithAttributeRecursive(element, attributeName, new ArrayList<Element>());
    }
    
    private static List<Element> findElementsWithAttributeRecursive(Element element, String attributeName, List<Element> holder) {
        if(element.attribute(attributeName) != null) {
            holder.add(element);
        }
        
        for(Iterator it = element.elements().iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            findElementsWithAttributeRecursive(element, attributeName, holder);
        }
        return holder;
    }
    
    
}
