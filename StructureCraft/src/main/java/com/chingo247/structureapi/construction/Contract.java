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
package com.chingo247.structureapi.construction;

import com.chingo247.structureapi.construction.fawe.FaweBlockSessionFactory;
import com.chingo247.structureapi.model.Traversal;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class Contract {
    
    private FaweBlockSessionFactory sessionFactory;
    private Traversal traversal;
    private UUID player;
    private boolean useForce, recursive, restrictive, reversed, useBackup;

    public Contract(FaweBlockSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.useBackup = true; // default
    }

    public FaweBlockSessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public Contract setStructureTraversal(Traversal traversal) {
        this.traversal = traversal;
        return this;
    }

    public Contract setForced(boolean useForce) {
        this.useForce = useForce;
        return this;
    }

    public void setUseBackup(boolean useBackup) {
        this.useBackup = useBackup;
    }

    public boolean useBackup() {
        return useBackup;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public boolean isRestrictive() {
        return restrictive;
    }

    public boolean isReversed() {
        return reversed;
    }

    public Contract setRestrictive(boolean restrictive) {
        this.restrictive = restrictive;
        return this;
    }

    public Contract setRecursive(boolean recursive) {
        this.recursive = recursive;
        return this;
    }

    public Contract setReversedOrder(boolean reversed) {
        this.reversed = reversed;
        return this;
    }

    public Contract setPlayer(UUID player) {
        this.player = player;
        return this;
    }

    public Traversal getStructureTraversal() {
        return traversal;
    }
    
}
