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
package com.chingo247.structurecraft;

import com.chingo247.structurecraft.services.IEconomyProvider;
import com.chingo247.xplatform.core.IServer;
import com.chingo247.xplatform.core.IWorld;
import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Chingo
 */
public class StructureValidator {

    private final IServer server;
    private final IEconomyProvider economy;
    

    private static final String LOCK_DATA = "lockData";

    public StructureValidator(IServer server, ExecutorService executor, IEconomyProvider economyProvider) {
        this.server = server;
        this.economy = economyProvider;
    }

    private File getSessionFile(IWorld world) {
        File wd = server.getWorldFolder(world.getName());
        return new File(wd, "session.lock");
    }

    public void invalidate() {
    }

}
