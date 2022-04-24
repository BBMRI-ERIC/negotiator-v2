/**
 * Copyright (c) 2017. Medizinische Informatik in der Translationalen Onkologie,
 * Deutsches Krebsforschungszentrum in Heidelberg
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.bbmri.negotiator.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import de.samply.bbmri.negotiator.Config;
import de.samply.bbmri.negotiator.ConfigFactory;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Collection;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Biobank;
import de.samply.bbmri.negotiator.jooq.tables.pojos.Person;
import eu.bbmri.eric.csit.service.negotiator.database.DbUtilCollection;

public class CollectionBiobankDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *The Biobank of the collections who made the offer.
     */
    private Biobank biobank;

    /**
     * The collection of the person who made the offer.
     */
    private Collection collection;

    public Biobank getBiobank() {
        return biobank;
    }

    public void setBiobank(Biobank biobank) {
        this.biobank = biobank;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public boolean isContacable() {
        if(getCollection() != null) {
            int collectioId = getCollection().getId();
            try(Config config = ConfigFactory.get()) {
                List<Person> listCollectionOwner = DbUtilCollection.getPersonsContactsForCollection(config, collectioId);
                if(listCollectionOwner.size() > 0) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof CollectionBiobankDTO){
            CollectionBiobankDTO toCompare = (CollectionBiobankDTO) o;
            return this.getBiobank().getId().equals(toCompare.getBiobank().getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getBiobank().getId().hashCode();
    }
}