/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class TrackingServiceStub implements TrackingService {

    // You add a project by adding an entry with an empty observable array list
    // of issue IDs in the projects Map.
    final ObservableMap<String, ObservableList<String>> projectsMap;
    {
        final Map<String, ObservableList<String>> map = new TreeMap<String, ObservableList<String>>();
        projectsMap = FXCollections.observableMap(map);
        
        for (String s : newList("Message Digest", "Chiffrement", "Key Generator", "Signature","Tchat !")) {
                    projectsMap.put(s, FXCollections.<String>observableArrayList());
        }
    }

    // The projectNames list is kept in sync with the project's map by observing
    // the projectsMap and modifying the projectNames list in consequence.
    final MapChangeListener<String, ObservableList<String>> projectsMapChangeListener = new MapChangeListener<String, ObservableList<String>>() {
        @Override
        public void onChanged(Change<? extends String, ? extends ObservableList<String>> change) {
            if (change.wasAdded()) projectNames.add(change.getKey());
            if (change.wasRemoved()) projectNames.remove(change.getKey());
        }
    };
    final ObservableList<String> projectNames;
    {
        projectNames = FXCollections.<String>observableArrayList();
        projectNames.addAll(projectsMap.keySet());
        projectsMap.addListener(projectsMapChangeListener);
    }

    
    final AtomicInteger issueCounter = new AtomicInteger(0);
//    final ObservableMap<String, IssueStub> issuesMap;

    private static <T> List<T> newList(T... items) {
        return Arrays.asList(items);
    }



    @Override
    public ObservableList<String> getProjectNames() {
        return projectNames;
    }



}
