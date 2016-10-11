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
package com.chingo247.structurecraft.util.functions;

/**
 * 
 * @author Chingo
 * @param <T> The class of the object which will be created
 * @param <K> The class of the first input variable
 * @param <L> The class of the second input variable
 * @param <M> The class of the third input variable 
 */
@FunctionalInterface
public interface ProduceWithInput3Val<T,K,L,M> {
    
    T create(K k, L l, M m);
    
}
