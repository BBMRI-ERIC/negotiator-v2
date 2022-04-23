/*
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

package de.samply.bbmri.negotiator.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.samply.bbmri.negotiator.model.CollectionBiobankDTO;
import de.samply.bbmri.negotiator.model.JsTreeJson;

import java.util.ArrayList;
import java.util.List;

public class ObjectToJson {
    /**
     * It takes a list of biobanks containing collections and and convert it to the json with help of the GSON
     *
     * @param  biobanksContainingCollections
     * @return String JSON data
     */
    public static String getJsonTree(List<CollectionBiobankDTO> biobanksContainingCollections) {
        List<JsTreeJson> jsTree = new ArrayList<>();
        List<JsTreeJson> childNodes;
        List<JsTreeJson> parentNodes;

        parentNodes = setParentNodes(biobanksContainingCollections);
        childNodes = setChildNodes(biobanksContainingCollections);
        jsTree.addAll(childNodes);
        jsTree.addAll(parentNodes);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsTreeData = gson.toJson(jsTree);

        return jsTreeData;
    }

    /**
     * To the set the Biobanks by setting its parent by '#'
     *
     * @param collectionBiobankDTOS
     * @return the list of parent node list
     */
    public static List<JsTreeJson> setParentNodes(List<CollectionBiobankDTO> collectionBiobankDTOS) {

        List<JsTreeJson> parentNodes = new ArrayList<>();
        List<CollectionBiobankDTO> uniqueBiobanks;
        uniqueBiobanks = getUniqueBiobanks(collectionBiobankDTOS);

        for(int i = 0; i < uniqueBiobanks.size(); i++) {
            JsTreeJson jsTreeJson = new JsTreeJson();
            jsTreeJson.setId(uniqueBiobanks.get(i).getBiobank().getId().toString());
            jsTreeJson.setParent("#");
            jsTreeJson.setText(uniqueBiobanks.get(i).getBiobank().getName());
            jsTreeJson.setIcon("fa fa-chevron-circle-right");
            parentNodes.add(jsTreeJson);
        }

        return parentNodes;
    }

    /**
     * This method is to remove the repeated biobanks name in the list of the CollectionBiobankDTO
     *
     * @param collectionBiobankDTOS
     * @return the list of names of biobanks which are unique
     */
    public static List<CollectionBiobankDTO> getUniqueBiobanks(List<CollectionBiobankDTO> collectionBiobankDTOS) {

        List<CollectionBiobankDTO> uniqueCollectionBiobankDTO = new ArrayList<>();

        for(int i = 0; i < collectionBiobankDTOS.size(); i++) {
            // Checking uniqueness based on biobank id
            if (!uniqueCollectionBiobankDTO.contains(collectionBiobankDTOS.get(i))) {
                uniqueCollectionBiobankDTO.add(collectionBiobankDTOS.get(i));
            }
        }

        return uniqueCollectionBiobankDTO;
    }

    /**
     * This method to set the Collections with the respected biobanks as their parents
     *
     * @param collectionBiobankDTOS
     * @return the list of collections with its correct biobanks associated with it
     */
    public static List<JsTreeJson> setChildNodes(List<CollectionBiobankDTO> collectionBiobankDTOS) {

        List<JsTreeJson> childNodes = new ArrayList<>();

        for(int i = 0; i < collectionBiobankDTOS.size(); i++) {

            JsTreeJson jsTreeJson = new JsTreeJson();
            jsTreeJson.setId("negoid_" + collectionBiobankDTOS.get(i).getCollection().getId());
            jsTreeJson.setParent(collectionBiobankDTOS.get(i).getBiobank().getId().toString());
            jsTreeJson.setText(collectionBiobankDTOS.get(i).getCollection().getName() + "");
            if(collectionBiobankDTOS.get(i).isContacable()) {
                jsTreeJson.setIcon("fa fa-envelope-open");
            } else {
                jsTreeJson.setIcon("fa fa-bell-slash-o");
            }


            childNodes.add(jsTreeJson);
        }

        return childNodes;
    }
}
