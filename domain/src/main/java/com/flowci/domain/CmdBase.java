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

import com.google.common.base.Strings;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author yang
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public abstract class CmdBase implements Serializable {

    @NonNull
    private String id;

    private Boolean allowFailure = Boolean.FALSE;

    /**
     * Cmd related plugin name
     */
    private String plugin;

    public boolean hasPlugin() {
        return !Strings.isNullOrEmpty(plugin);
    }
}
