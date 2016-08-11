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
package com.chingo247.structureapi.util;

import com.chingo247.structureapi.StructureCraft;
import com.sk89q.worldedit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Chingo
 */
public class ErrorHandler {
    
    private static final Map<Class, ErrorHandler> handlers = new HashMap<>();
    
    private Class<?> clazz;
    

    ErrorHandler(Class<?> clazz) {
    }
    
    public void handle(@Nullable Exception exception, String message, Player player) {
        if(player != null) player.printError(message);
        if(exception != null) Logger.getLogger(clazz.getName()).log(Level.SEVERE, exception.getMessage(), exception);
    }
    
    
    public static synchronized @Nonnull ErrorHandler getHandler(Class<?> clazz) {
        ErrorHandler handler = handlers.get(clazz);
        if(handler == null) {
            handler = new ErrorHandler(clazz);
            handlers.put(clazz, handler);
        }
        return handler;
    }
    
}
