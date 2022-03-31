package eu.bbmri.eric.csit.service.negotiator.database;

import de.samply.bbmri.negotiator.Config;

public class DbUtilAdmin {
    public static void toggleRequestTestState(Config config, Integer queryId) {
        config.dsl().execute("UPDATE public.query SET test_request= NOT test_request WHERE id=" + queryId);
    }
}
