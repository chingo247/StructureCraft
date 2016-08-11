package com.chingo247.structureapi.io.schematic;


import com.google.common.base.Preconditions;
import java.io.File;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchematicProcessor extends RecursiveTask<ISchematic> {

    private static final SchematicReader SCHEMATIC_READER = new SchematicReader();
    private final File schematicFile;

    public SchematicProcessor(File schematic) {
        Preconditions.checkNotNull(schematic);
        Preconditions.checkArgument(schematic.exists());
        this.schematicFile = schematic;
    }

    @Override
    protected ISchematic compute() {
        try {
            return SCHEMATIC_READER.readSchematic(schematicFile);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

}
