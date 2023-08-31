package de.groodian.hyperiorcore.util;

import de.groodian.hyperiorcore.main.HyperiorCore;
import java.sql.SQLException;
import java.util.List;

public class DatabaseManagerPaper extends DatabaseManager {
    public DatabaseManagerPaper(String hostname, int port, String database, String username, String password) {
        super(hostname, port, database, username, password);
    }

    public synchronized void transaction(List<DatabaseTransaction> databaseTransactions, DatabaseTransactionCallback callback) {
        new Task(HyperiorCore.getPaper()) {
            @Override
            public void executeAsync() {
                DatabaseConnection databaseConnection = getConnection();

                try {
                    databaseConnection.getConnection().setAutoCommit(false);

                    boolean error = false;
                    for (DatabaseTransaction databaseTransaction : databaseTransactions) {
                        if (!databaseTransaction.run(databaseConnection)) {
                            error = true;
                            break;
                        }
                    }

                    if (error) {
                        databaseConnection.getConnection().rollback();
                    } else {
                        databaseConnection.getConnection().commit();
                    }

                    cache.add(error);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                databaseConnection.finish();
            }

            @Override
            public void executeSyncOnFinish() {
                if ((boolean) cache.get(0)) {
                    if (callback != null) {
                        callback.finished(false);
                    }
                } else {
                    for (DatabaseTransaction databaseTransaction : databaseTransactions) {
                        databaseTransaction.runOnSuccess();
                    }

                    if (callback != null) {
                        callback.finished(true);
                    }
                }
            }
        };
    }

}
