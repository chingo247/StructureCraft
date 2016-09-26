package com.chingo247.structurecraft.io.schematic;


import com.google.common.base.Preconditions;
import java.io.File;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchematicProcessor extends RecursiveTask<ISchematic> {

    private final File schematicFile;

    public SchematicProcessor(File schematic) {
        Preconditions.checkNotNull(schematic);
        Preconditions.checkArgument(schematic.exists());
        this.schematicFile = schematic;
    }

    @Override
    protected ISchematic compute() {
        try {
            return Schematic.readSchematic(schematicFile);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

}
