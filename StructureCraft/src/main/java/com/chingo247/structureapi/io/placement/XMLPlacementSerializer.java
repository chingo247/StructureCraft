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
package com.chingo247.structureapi.io.placement;

import com.chingo247.structureapi.model.placement.IPlacement;
import com.chingo247.structureapi.io.annotations.XMLDeserializer;
import com.chingo247.structureapi.io.annotations.XMLSerializer;
import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.dom4j.Element;

/**
 *
 * @author Chingo
 */
public class XMLPlacementSerializer<T extends IPlacement> {
    
    private Method serialize;
    private XMLSerializer serializer;

    public XMLPlacementSerializer(Method load, XMLSerializer saver) {
        Preconditions.checkArgument(Modifier.isStatic(load.getModifiers()), "Saver method must be static!");
        this.serialize = load;
        this.serializer = saver;
    }

    public XMLSerializer getSerializerMeta() {
        return serializer;
    }
    
    public Element serialize(T placement, Element rootElement) {
        try {
            return (Element) serialize.invoke(null, placement, rootElement);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
