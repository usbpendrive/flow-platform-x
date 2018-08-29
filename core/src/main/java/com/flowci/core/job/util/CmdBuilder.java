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

package com.flowci.core.job.util;

import com.flowci.core.job.domain.Job;
import com.flowci.domain.Cmd;
import com.flowci.domain.VariableMap;
import com.flowci.tree.Node;
import com.google.common.collect.Lists;

/**
 * @author yang
 */
public class CmdBuilder {

    public static Cmd build(Job job, Node node) {
        VariableMap variables = node.getEnvironments().merge(job.getContext());

        Cmd cmd = new Cmd(KeyBuilder.buildCmdKey(job, node));
        cmd.setInputs(variables);
        cmd.setScripts(Lists.newArrayList(node.getScript()));
        return cmd;
    }

    private CmdBuilder() {
    }
}
