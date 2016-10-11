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
package com.chingo247.structurecraft.xplatform.core.item;

/**
 * Abstraction of ItemStack
 * @author Chingo
 */
public abstract class AItemstack implements IItemStack {

    
    /**
     * Checks if this item matches another
     * @param other The other item
     * @return True if item matches material, data and lore
     */
    @Override
    public boolean matches(AItemstack other) {
        return getMaterial() == other.getMaterial() 
                    && getData() == other.getData()
                    && other.getLore().equals(getLore());
    }

}
