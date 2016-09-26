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
package com.chingo247.structurecraft.model.structure;

import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class StructureSaleDetails {
    
    private long structure;
    private boolean isForSale;
    private double price;
    private UUID sellerUUID;
    private String sellerName;

    public StructureSaleDetails(long structure, boolean isForSale, double price, UUID sellerUUID, String sellerName) {
        this.structure = structure;
        this.isForSale = isForSale;
        this.price = price;
        this.sellerUUID = sellerUUID;
        this.sellerName = sellerName;
    }

    public long getStructure() {
        return structure;
    }

    public boolean isIsForSale() {
        return isForSale;
    }

    public double getPrice() {
        return price;
    }

    public UUID getSellerUUID() {
        return sellerUUID;
    }

    public String getSellerName() {
        return sellerName;
    }

    
    
    
}
