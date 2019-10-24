package de.samply.bbmri.negotiator.util;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.db.util.DbUtil;
import de.samply.bbmri.negotiator.jooq.tables.records.BiobankRecord;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class DataCache {
    private static DataCache dataCache = null;

    List<BiobankRecord> biobankRecords_ = null;
    HashMap<Integer, String> biobankNames_ = null;

    private DataCache() {

    }

    public static DataCache getInstance() {
        if(dataCache == null) {
            synchronized (DataCache.class) {
                dataCache = new DataCache();
            }
        }
        return dataCache;
    }

    public void createUpdateBiobankList() {
        List<BiobankRecord> biobankRecords = null;
        HashMap<Integer, String> biobankNames = new HashMap<Integer, String>();
        try(Config config = ConfigFactory.get()) {
            biobankRecords = DbUtil.getBiobanks(config);
            for(BiobankRecord biobankRecord : biobankRecords) {
                biobankNames.put(biobankRecord.getId(), biobankRecord.getName());
            }
            biobankRecords_ = biobankRecords;
            biobankNames_ = biobankNames;
        } catch (SQLException e) {
            System.err.println("ERROR: ResearcherQueriesBean::getPrivateNegotiationCountAndTime(int index)");
            e.printStackTrace();
        }
    }

    public String getBiobankName(Integer biobankId) {
        if(biobankNames_ == null) {
            createUpdateBiobankList();
        }
        if(biobankNames_.containsKey(biobankId)) {
            return biobankNames_.get(biobankId);
        }
        return "";
    }
}
