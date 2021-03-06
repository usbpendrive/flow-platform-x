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

package com.flowci.core.test.job;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowci.core.domain.JsonablePage;
import com.flowci.core.domain.StatusCode;
import com.flowci.core.job.domain.CreateJob;
import com.flowci.core.job.domain.Job;
import com.flowci.core.test.MvcMockHelper;
import com.flowci.core.test.SpringScenario;
import com.flowci.domain.ExecutedCmd;
import com.flowci.domain.ExecutedCmd.Status;
import com.flowci.domain.http.ResponseMessage;
import com.flowci.util.StringHelper;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;

/**
 * @author yang
 */
@FixMethodOrder(value = MethodSorters.JVM)
public class JobControllerTest extends SpringScenario {

    private static final TypeReference<ResponseMessage<Job>> JobType =
        new TypeReference<ResponseMessage<Job>>() {
        };

    private static final TypeReference<ResponseMessage<JsonablePage<Job>>> JobListType =
        new TypeReference<ResponseMessage<JsonablePage<Job>>>() {
        };

    private static final TypeReference<ResponseMessage<List<ExecutedCmd>>> JobStepsType =
        new TypeReference<ResponseMessage<List<ExecutedCmd>>>() {
        };

    @Autowired
    private MvcMockHelper mvcMockHelper;

    @Autowired
    private ObjectMapper objectMapper;

    private final String flow = "hello-flow";

    @Before
    public void init() throws Exception {
        mockLogin();
        createFlow(flow);
    }

    @Test
    public void should_get_job_yml() throws Exception {
        createJobForFlow(flow);

        String yml = mvcMockHelper.expectSuccessAndReturnString(get("/jobs/hello-flow/1/yml"));
        Assert.assertNotNull(yml);
        Assert.assertEquals(StringHelper.toString(load("flow.yml")), yml);
    }

    @Test
    public void should_get_job_by_name_and_build_number() throws Exception {
        // init: create job
        Job created = createJobForFlow(flow);
        Assert.assertNotNull(created);

        // when:
        ResponseMessage<Job> response = mvcMockHelper.expectSuccessAndReturnClass(get("/jobs/hello-flow/1"), JobType);
        Assert.assertEquals(StatusCode.OK, response.getCode());

        // then:
        Job loaded = response.getData();
        Assert.assertNotNull(loaded);
        Assert.assertEquals(created, loaded);
    }

    @Test
    public void should_get_latest_job() throws Exception {
        // init:
        Job first = createJobForFlow(flow);
        Assert.assertEquals(1, first.getBuildNumber().intValue());

        Job second = createJobForFlow(flow);
        Assert.assertEquals(2, second.getBuildNumber().intValue());

        // when:
        ResponseMessage<Job> response =
            mvcMockHelper.expectSuccessAndReturnClass(get("/jobs/hello-flow/latest"), JobType);
        Assert.assertEquals(StatusCode.OK, response.getCode());

        // then:
        Job latest = response.getData();
        Assert.assertNotNull(latest);
        Assert.assertEquals(2, latest.getBuildNumber().intValue());
    }

    @Test
    public void should_list_job_by_flow() throws Exception {
        // init:
        Job first = createJobForFlow(flow);
        Job second = createJobForFlow(flow);

        // when:
        ResponseMessage<JsonablePage<Job>> message = mvcMockHelper
            .expectSuccessAndReturnClass(get("/jobs/hello-flow"), JobListType);
        Assert.assertEquals(StatusCode.OK, message.getCode());

        // then:
        Page<Job> page = message.getData().toPage();
        Assert.assertEquals(2, page.getTotalElements());

        Assert.assertEquals(second, page.getContent().get(0));
        Assert.assertEquals(first, page.getContent().get(1));
    }

    @Test
    public void should_list_job_steps_by_flow_and_build_number() throws Exception {
        // init:
        createJobForFlow(flow);

        // when:
        ResponseMessage<List<ExecutedCmd>> message = mvcMockHelper
            .expectSuccessAndReturnClass(get("/jobs/hello-flow/1/steps"), JobStepsType);
        Assert.assertEquals(StatusCode.OK, message.getCode());

        // then:
        List<ExecutedCmd> steps = message.getData();
        Assert.assertEquals(2, steps.size());
        Assert.assertEquals(Status.PENDING, steps.get(0).getStatus());
        Assert.assertEquals(Status.PENDING, steps.get(1).getStatus());
    }

    public Job createJobForFlow(String name) throws Exception {
        return mvcMockHelper.expectSuccessAndReturnClass(post("/jobs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(new CreateJob(name))), JobType)
            .getData();
    }

    public void createFlow(String name) throws Exception {
        String yml = StringHelper.toString(load("flow.yml"));

        ResponseMessage message = mvcMockHelper.expectSuccessAndReturnClass(post("/flows/" + name)
            .contentType(MediaType.TEXT_PLAIN)
            .content(yml), ResponseMessage.class);

        Assert.assertEquals(StatusCode.OK, message.getCode());
    }
}
