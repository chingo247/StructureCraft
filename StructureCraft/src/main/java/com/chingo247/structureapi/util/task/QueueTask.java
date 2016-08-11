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

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public abstract class QueueTask<T> {

    protected boolean failed = false, cancelled = false;
    private T id;
    private QueueTask<T> nextTask;
    private List<Runnable> onComplete, onFailed, onCancel;

    public QueueTask(T id) {
        this.id = id;
    }

    /**
     * Creates an iterator to iterate over all tasks starting with this task.
     *
     * @return The iterator
     */
    public Iterator<QueueTask<T>> iterate() {
        return new QueueTaskIterator(this);
    }

    public void queue(QueueTask<T> task) {
        if (this.id.equals(task.id) || (nextTask != null && nextTask.id.equals(task.id))) {
            throw new IllegalArgumentException("Can't task with id '" + task.id + " already in queue");
        }

        if (nextTask != null) {
            nextTask.queue(task);
        } else {
            nextTask = task;
        }
    }

    public T getId() {
        return id;
    }

    public boolean hasFailed() {
        return failed;
    }

    public void onCompleted(Runnable executedOnSuccess) {
        if (this.onComplete == null) {
            onComplete = new ArrayList<>();
        }
        this.onComplete.add(executedOnSuccess);
    }

    public void onFailed(Runnable executedOnFail) {
        if (this.onFailed == null) {
            onFailed = new ArrayList<>();
        }
        this.onFailed.add(executedOnFail);
    }

    public void onCancel(Runnable executedOnCancel) {
        if (this.onCancel == null) {
            this.onCancel = new ArrayList<>();
        }
        this.onCancel.add(executedOnCancel);
    }

    public void cancel() {
        synchronized (this) {
            if (!cancelled) {
                onCancel.forEach(task -> {
                    try {
                        task.run();
                    } catch (Exception ex) {
                        Logger.getLogger(QueueTask.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                });
                cancelled = true;
            }
        }

        if (nextTask != null) {
            nextTask.cancel();
        }
    }

    protected abstract void task() throws Exception;

    public void execute() {
        synchronized (this) {
            if (!cancelled) return;
        }
        boolean success = false;
        try {
            task();
            success = true;
        } catch (Exception ex) {
            Logger.getLogger(QueueTask.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (success) {
            onSuccess();
        } else {
            onFailed();
        }

    }

    protected void onSuccess() {
        if (this.onComplete != null) {
            onComplete.forEach(task -> {
                try {
                    task.run();
                } catch (Exception ex) {
                    Logger.getLogger(QueueTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        if (nextTask != null) {
            nextTask.execute();
        }
    }

    protected void onFailed() {
        failed = true;
        if (this.onFailed != null) {
            onFailed.forEach(task -> {
                try {
                    task.run();
                } catch (Exception ex) {
                    Logger.getLogger(QueueTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
        if (nextTask != null) {
            nextTask.onFailed();
        }
    }

    private class QueueTaskIterator implements Iterator<QueueTask<T>> {

        private QueueTask<T> current;

        public QueueTaskIterator(QueueTask<T> current) {
            Preconditions.checkNotNull(current, "current task may not be null");
            this.current = current;
        }

        @Override
        public boolean hasNext() {
            return current.nextTask != null;
        }

        @Override
        public QueueTask<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No next element!");
            }
            current = current.nextTask;
            return current;
        }

    }

}
