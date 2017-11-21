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
     * It add collections and biobanks list and convert it to the json with help of the GSON
     *
     * @param collections
     * @return String JSON data
     */
    public static String jsonTree(List<CollectionBiobankDTO> collections) {
        List<JsTreeJson> stats = new ArrayList<>();
        List<JsTreeJson> statsChildNode;
        List<JsTreeJson> statsParentNode;

        statsChildNode = setParentNode(collections);
        statsParentNode = setChildNode(collections);
        stats.addAll(statsParentNode);
        stats.addAll(statsChildNode);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsTreeData = gson.toJson(stats);
        return jsTreeData;
    }

    /**
     * To the set the Biobanks by setting its parent by '#'
     *
     * @param collectionBiobankDTOS
     * @return the list of parent node list
     */
    public static List<JsTreeJson> setParentNode(List<CollectionBiobankDTO> collectionBiobankDTOS) {

        List<JsTreeJson> setParentNode = new ArrayList<>();
        List<String> biobankNameList;
        biobankNameList = getNewList(collectionBiobankDTOS);

        for(int i = 0; i < biobankNameList.size(); i++) {
            JsTreeJson stat = new JsTreeJson();
            stat.setId(biobankNameList.get(i));
            stat.setParent("#");
            stat.setText(biobankNameList.get(i));
            stat.setIcon("/");
            setParentNode.add(stat);
        }

        return setParentNode;
    }

    /**
     * This method is to remove the repeated biobanks name in the list of the CollectionBiobankDTO
     *
     * @param collectionBiobankDTOS
     * @return the list of names of biobanks which are unique
     */
    public static List<String> getNewList(List<CollectionBiobankDTO> collectionBiobankDTOS) {
        List<String> oldList = new ArrayList<>();
        List<String> newList = new ArrayList<>();

        for(int i = 0; i < collectionBiobankDTOS.size(); i++) {
            oldList.add(collectionBiobankDTOS.get(i).getBiobank().getName());
        }

        for(int i = 0; i < oldList.size(); i++) {
            String str = oldList.get(i);
            if(oldList.contains(str) && !newList.contains(str))
                newList.add(str);
        }

        return newList;
    }

    /**
     * This method to set the Collections with the respected biobanks as their parents
     *
     * @param collectionBiobankDTOS
     * @return the list of collections with its correct biobanks associated with it
     */
    public static List<JsTreeJson> setChildNode(List<CollectionBiobankDTO> collectionBiobankDTOS) {
        List<JsTreeJson> setChildNode = new ArrayList<>();

        for(int i = 0; i < collectionBiobankDTOS.size(); i++) {
            JsTreeJson stat = new JsTreeJson();
            stat.setId(collectionBiobankDTOS.get(i).getCollection().getName());
            stat.setParent(collectionBiobankDTOS.get(i).getBiobank().getName());
            stat.setText(collectionBiobankDTOS.get(i).getCollection().getName());
            stat.setIcon("/");
            setChildNode.add(stat);
        }

        return setChildNode;
    }
}
