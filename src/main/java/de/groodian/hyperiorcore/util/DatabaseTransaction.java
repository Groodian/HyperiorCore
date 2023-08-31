package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.HyperiorCore;
import de.groodian.hyperiorcore.main.Main;

public abstract class DatabaseTransaction {

    protected final Main plugin;

    public DatabaseTransaction() {
        this.plugin = HyperiorCore.getPaper();
    }

    public abstract void runOnSuccess();

    public abstract boolean run(DatabaseConnection databaseConnection);

}
