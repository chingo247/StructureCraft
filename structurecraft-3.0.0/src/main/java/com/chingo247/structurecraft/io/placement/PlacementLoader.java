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
package com.chingo247.structurecraft.io.placement;

import com.chingo247.structurecraft.model.placement.IPlacement;
import com.chingo247.structurecraft.io.annotations.XMLSerializer;
import com.google.common.base.Preconditions;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author Chingo
 */
class PlacementLoader {
    
    private Method load;
    private XMLSerializer loader;

    public PlacementLoader(Method load, XMLSerializer loader) {
        Preconditions.checkArgument(Modifier.isStatic(load.getModifiers()), "Loader method must be static!");
        this.load = load;
        this.loader = loader;
    }
    
    public IPlacement load(File file) {
        try {
            return (IPlacement) load.invoke(null, file);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
