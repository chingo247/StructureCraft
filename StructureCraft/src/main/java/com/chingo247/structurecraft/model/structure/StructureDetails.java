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

import java.sql.Timestamp;
import java.util.Date;
import javax.annotation.Nullable;

/**
 *
 * @author Chingo
 */
public class StructureDetails {
    
    private Long id;
    private String name;
    private ConstructionStatus status;
    private Timestamp completedAt, createdAt, deletedAt;
    private double refundValue;

    public StructureDetails(Long id, String name, ConstructionStatus status, double refundValue, Timestamp completedAt, Timestamp createdAt, Timestamp deletedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.completedAt = completedAt;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.deletedAt = deletedAt;
        this.refundValue = refundValue;
    }

    /**
     * Gets the id of the structure.
     * @return The id
     */
    public Long getId() {
        return id;
    }

    public double getRefundValue() {
        return refundValue;
    }
    

    /**
     * Gets the name of the structure
     * @return The name of the structure
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the status of the structure.
     * @return the status of the structure
     */
    public ConstructionStatus getStatus() {
        return status;
    }

    /**
     * Gets the date at which the structure was completed.
     * @return the date
     */
    public @Nullable Timestamp getCompletedAt() {
        return completedAt;
    }

    /**
     * Gets the date at which the structure was created.
     * @return The date
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the date at which the structure was deleted.
     * @return The date.
     */
    public @Nullable Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(ConstructionStatus status) {
        this.status = status;
    }

    public void setRefundValue(double refundValue) {
        this.refundValue = refundValue;
    }

    public void setId(Long structureId) {
        this.id = structureId;
    }

    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    

    
    
}
