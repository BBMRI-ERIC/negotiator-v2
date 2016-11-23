package de.samply.bbmri.negotiator.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import de.samply.common.sql.SQLUtil;
import de.samply.common.upgrade.SamplyUpgradeException;
import de.samply.common.upgrade.UpgradeExecution;

/**
 * Example Upgrade Execution
 */
public class UpgradeExecutionExample extends UpgradeExecution<Void> {

    public UpgradeExecutionExample(Connection dbConnection) {
        super(dbConnection);
    }

    @Override
    public Boolean doUpgrade() throws SamplyUpgradeException, SQLException {
        try {
            SQLUtil.executeSQLFile(getDbConnection(), getClass().getClassLoader(), "/sql/upgrades/example.sql");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
