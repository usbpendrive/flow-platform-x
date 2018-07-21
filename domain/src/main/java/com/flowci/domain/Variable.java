/*
 * Copyright 2018 fir.im
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
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author yang
 */
@ToString(of = {"name", "valueType"})
@EqualsAndHashCode(of = {"name"})
@NoArgsConstructor
public class Variable implements Serializable {

    public enum ValueType {

        STRING,

        INT,

        LIST
    }

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String alias;

    @Getter
    @Setter
    private ValueType valueType = ValueType.STRING;

    /**
     * Available values
     */
    @Getter
    @Setter
    private List<String> values;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, ValueType valueType) {
        this.name = name;
        this.valueType = valueType;
    }
}