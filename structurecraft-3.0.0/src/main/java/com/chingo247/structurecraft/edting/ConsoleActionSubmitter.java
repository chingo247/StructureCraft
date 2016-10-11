/*
 * Copyright (C) 2016 ching
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
package com.chingo247.structurecraft.edting;

import com.chingo247.structurecraft.StructureCraft;
import com.chingo247.structurecraft.xplatform.core.IColors;
import com.chingo247.structurecraft.xplatform.core.ICommandSender;
import java.util.UUID;

/**
 *
 * @author ching
 */
public class ConsoleActionSubmitter implements ActionSubmitter {
    
    private static final UUID CONSOLE = UUID.randomUUID();
    
    private IColors colors;
    private ICommandSender sender;

    public ConsoleActionSubmitter() {
        this.colors = StructureCraft.IMP.getPlatform().getChatColors();
        this.sender = StructureCraft.IMP.getPlatform().getConsole();
    }

    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

    @Override
    public void sendError(String message) {
        this.sender.sendMessage(colors.red() + message);
    }

    @Override
    public UUID getUniqueId() {
        return CONSOLE;
    }

    
    
}
