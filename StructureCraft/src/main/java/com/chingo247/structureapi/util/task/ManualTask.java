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
package com.chingo247.structureapi.util.task;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines a task where the succes/fail state should be called by using their respective methods ( succes() / fail() )
 * @author Chingo
 */
public abstract class ManualTask<T> extends QueueTask<T> {

    public ManualTask(T id) {
        super(id);
    }

    @Override
    protected abstract void task() throws Exception;

    @Override
    public void execute()  {
        synchronized (this) {
            if (!cancelled) return;
        }
        try {
            task();
        } catch (Exception ex) {
            Logger.getLogger(QueueTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void fail() {
        onFailed();
    }
    
    public void succes() {
        onSuccess();
    }
    
}
