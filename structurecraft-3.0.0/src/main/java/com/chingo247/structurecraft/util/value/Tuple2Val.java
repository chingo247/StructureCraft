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
package com.chingo247.structurecraft.util.value;

/**
 *
 * @author Chingo247
 */
public class Tuple2Val<V,K> {
    
    private V v;
    private K k;

    public Tuple2Val(V item1, K item2) {
        this.v = item1;
        this.k = item2;
    }

    public K getItem2() {
        return k;
    }

    public V getItem1() {
        return v;
    }

}
