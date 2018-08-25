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

package com.flowci.core.test.flow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowci.core.domain.RequestMessage;
import com.flowci.core.domain.ResponseMessage;
import com.flowci.core.domain.StatusCode;
import com.flowci.core.flow.FlowService;
import com.flowci.core.flow.domain.Flow;
import com.flowci.core.test.MvcMockHelper;
import com.flowci.core.test.SpringScenario;
import com.flowci.util.StringHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

/**
 * @author yang
 */
public class FlowControllerTest extends SpringScenario {

    private final static TypeReference<ResponseMessage<Flow>> FlowType =
        new TypeReference<ResponseMessage<Flow>>() {
        };

    private final static TypeReference<ResponseMessage<List<Flow>>> ListFlowType =
        new TypeReference<ResponseMessage<List<Flow>>>() {
        };

    private final static TypeReference<ResponseMessage<String>> YmlType =
        new TypeReference<ResponseMessage<String>>() {
        };

    @Autowired
    private MvcMockHelper mvcMockHelper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlowService flowService;

    private final String flowName = "hello_world";

    @Before
    public void login() {
        mockLogin();
    }

    @Before
    public void createFlowWithYml() throws Exception {
        String yml = StringHelper.toString(load("flow.yml"));
        String jsonContent = objectMapper.writeValueAsString(new RequestMessage<>(yml));

        ResponseMessage<Flow> response = mvcMockHelper
            .expectSuccessAndReturnClass(post("/flows/" + flowName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent), FlowType);

        Assert.assertEquals(StatusCode.OK, response.getCode());
    }

    @Test
    public void should_get_flow_and_yml() throws Exception {
        ResponseMessage<Flow> getFlowResponse = mvcMockHelper
            .expectSuccessAndReturnClass(get("/flows/" + flowName), FlowType);

        Assert.assertEquals(StatusCode.OK, getFlowResponse.getCode());
        Assert.assertEquals(flowName, getFlowResponse.getData().getName());

        ResponseMessage<String> getYmlResponse = mvcMockHelper
            .expectSuccessAndReturnClass(get("/flows/" + flowName + "/yml"), YmlType);

        Assert.assertEquals(StatusCode.OK, getYmlResponse.getCode());
        Assert.assertEquals(StringHelper.toString(load("flow.yml")), getYmlResponse.getData());
    }

    @Test
    public void should_list_flows() throws Exception {
        ResponseMessage<List<Flow>> listFlowResponse = mvcMockHelper
            .expectSuccessAndReturnClass(get("/flows"), ListFlowType);

        Assert.assertEquals(StatusCode.OK, listFlowResponse.getCode());
        Assert.assertEquals(1, listFlowResponse.getData().size());
        Assert.assertEquals(flowName, listFlowResponse.getData().get(0).getName());
    }

    @Test
    public void should_update_and_clean_variables() throws Exception {
        // init
        Map<String, String> vars = new HashMap<>();
        vars.put("test", "result");
        vars.put("hello", "world");

        // when: update variables
        ResponseMessage updateVarResponse = mvcMockHelper.expectSuccessAndReturnClass(
            patch("/flows/" + flowName + "/variables")
                .content(objectMapper.writeValueAsString(vars))
                .contentType(MediaType.APPLICATION_JSON),
            ResponseMessage.class);

        Assert.assertEquals(StatusCode.OK, updateVarResponse.getCode());

        // then:
        Flow flow = flowService.get(flowName);
        Assert.assertEquals("result", flow.getVariables().getString("test"));
        Assert.assertEquals("world", flow.getVariables().getString("hello"));

        // when: clean variables
        ResponseMessage cleanVarResponse = mvcMockHelper.expectSuccessAndReturnClass(
            delete("/flows/" + flowName + "/variables"), ResponseMessage.class);

        Assert.assertEquals(StatusCode.OK, cleanVarResponse.getCode());


        // then:
        flow = flowService.get(flowName);
        Assert.assertTrue(flow.getVariables().isEmpty());
    }

}