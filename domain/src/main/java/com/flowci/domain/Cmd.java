/*
 * Copyright 2018 flow.ci
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flowci.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author yang
 */
@EqualsAndHashCode(of = {"id"})
public class Cmd implements Serializable {

    @Getter
    @Setter
    @NonNull
    private String id;

    @Getter
    @Setter
    @NonNull
    private List<String> scripts;

    @Getter
    @Setter
    private String workDir;

    /**
     * Cmd timeout in seconds
     */
    @Getter
    @Setter
    @NonNull
    private Long timeout = 1800L;

    @Getter
    @Setter
    @NonNull
    private VariableMap inputs = new VariableMap();

    /**
     * Output env filters
     */
    @Getter
    @Setter
    @NonNull
    private Set<String> envFilters = Collections.emptySet();

    public Cmd(String id) {
        this.id = id;
    }
}