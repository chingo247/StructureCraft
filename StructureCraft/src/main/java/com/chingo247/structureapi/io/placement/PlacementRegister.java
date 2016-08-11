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
import com.chingo247.structureapi.exception.placement.PlacementNotRegisteredException;
import com.chingo247.structureapi.exception.placement.PlacementRegisterException;
import com.chingo247.structureapi.io.annotations.XMLDeserializer;
import com.chingo247.structureapi.io.annotations.XMLSerializer;
import com.google.common.collect.Maps;
import fiber.core.impl.xml.located.LocatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 */
public class PlacementRegister {
    
    private static PlacementRegister instance;
    private Map<Class, XMLPlacementSerializer> serializers;
    private Map<Class, XMLPlacementDeserializer> deserializers;
    private Map<String, Class> registered; 
    
    public PlacementRegister() {
        this.serializers = Maps.newHashMap();
        this.deserializers = Maps.newHashMap();
        this.registered = Maps.newHashMap();
    }
    
    public static PlacementRegister instance() {
        if(instance == null) {
            instance = new PlacementRegister();
        }
        return instance;
    }
    
    public <T extends IPlacement> void register(Class<T> placmentType) throws PlacementRegisterException {
        
        Method serializer = null;
        Method deserializer = null;
        for(Method m : placmentType.getMethods()) {
            if (m.isAnnotationPresent(XMLDeserializer.class)) {
                deserializer = m;
                if(!m.getReturnType().equals(placmentType)) {
                    throw new PlacementRegisterException("Failed to register "+placmentType.getName()+": Deserialize method's return type must be equal to " + placmentType.getSimpleName());
                }
                
            }
            if (m.isAnnotationPresent(XMLSerializer.class)) {
                serializer = m;
            }
        }
        if (serializer == null) {
            throw new PlacementRegisterException("Failed to register placement: Missing XMLSerializer method");
        }
        if (deserializer == null) {
            throw new PlacementRegisterException("Failed to register placement: Missing XMLDeserializer method");
        }

        // Register the placements
        XMLSerializer serializeAnnotation = serializer.getAnnotation(XMLSerializer.class);
        XMLPlacementSerializer<T> xmlSerializer = new XMLPlacementSerializer<>(serializer, serializeAnnotation);
        PlacementRegister.instance().serializers.put(placmentType, xmlSerializer);
        XMLPlacementDeserializer<T> xmlDeserializer = new XMLPlacementDeserializer(deserializer, deserializer.getAnnotation(XMLDeserializer.class));
        PlacementRegister.instance().deserializers.put(placmentType, xmlDeserializer);
        
        String key = serializeAnnotation.plugin() + "." + serializeAnnotation.type();
        registered.put(key, placmentType);
    }
    
    
    public boolean isRegistered(String plugin, String typeName) {
        return registered.containsKey(plugin);
    }
    
    public boolean isRegistered(Class<? extends IPlacement> placementType) {
        if (!PlacementRegister.instance().serializers.containsKey(placementType)) {
            return false;
        }
        return PlacementRegister.instance().deserializers.containsKey(placementType);
    }
   
    public <T extends IPlacement> Element serialize(T placement) throws PlacementNotRegisteredException {
        XMLPlacementSerializer serializer = serializers.get(placement.getClass());
        if(serializer == null) {
            throw new PlacementNotRegisteredException("Failed to serialize " + placement.getClass().getName() + ": Placement was not registered");
        }
        Element root = new BaseElement("Placement");
        root.addAttribute("plugin", serializer.getSerializerMeta().plugin());
        root.addAttribute("type", serializer.getSerializerMeta().type());
        return serialize(placement, root);
    }
    
    public <T extends IPlacement> Element serialize(T placement, Element rootElement) throws PlacementNotRegisteredException {
        Class type = placement.getClass();
        if (serializers.containsKey(type)) {
            XMLPlacementSerializer<T> serializer =  serializers.get(type);
            return serializer.serialize(placement, rootElement);
        } 
        throw new PlacementNotRegisteredException("Failed to serialize " + placement.getClass().getName() + ": Placement was not registered");
    }
    
    public <T extends IPlacement> T deserialize(XMLElement element) throws PlacementNotRegisteredException {
        String plugin;
        String type;
        if(element.getElement().attribute("plugin") != null && element.getElement().attribute("type") != null) {
            LocatedElement le = element.getElement();
            plugin = le.attributeValue("plugin");
            type = le.attributeValue("type");
        } else {
            Node pluginNode = element.getElement().selectSingleNode("Plugin");
            Node typeNode = element.getElement().selectSingleNode("Type");
            plugin = pluginNode != null ? pluginNode.getText() : null;
            type = typeNode != null ? typeNode.getText() : null;
        }
        Class<T> clazz = registered.get(plugin + "." + type);
        if (clazz != null) {
            deserialize(element, clazz);
        } 
        throw new PlacementNotRegisteredException("Failed to deserialize xml element: Unknown type '" + type + "'");
    }
    
    public <T extends IPlacement> T deserialize(XMLElement element, Class<T> placementClass) throws PlacementNotRegisteredException {
        if (serializers.containsKey(placementClass)) {
            XMLPlacementDeserializer<T> deserializer =  deserializers.get(placementClass);
            return deserializer.deserialize(element.getElement());
        } 
        throw new PlacementNotRegisteredException("Failed to deserialize xml element: Unknown type '" + placementClass.getName() + "'");
    }
    
    
    
}
