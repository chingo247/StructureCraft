/*
 * Copyright (C) 2016 Chingo247
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
package com.chingo247.structurecraft.editing.context;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Chingo247
 */
public abstract class Context {
    
    private Map<String, Object> context;

    public Context() {
        this.context = new HashMap<>();
    }
    
    public void set(String key, Object object) {
        this.context.put(key, object);
    }
    
    public <T> T get(String key, Class<T> clazz) {
        Object o = context.get(key);
        if(o != null) {
            return clazz.cast(o);
        }
        return null;
    }
    
    public Object get(String key) {
        return context.get(key);
    }
    
    public int getInt(String key) {
        return (int) context.get(key);
    }
    
    public String getString(String key) {
        return (String) context.get(key);
    }
    
    public long getLong(String key) {
        return (long) context.get(key);
    }
    
    public boolean getBoolean(String key) {
        return (boolean) context.get(key);
    }
    
    public double getDouble(String key) {
        return (double) context.get(key);
    }
    
    
}
