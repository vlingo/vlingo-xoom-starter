// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.starter.task.template.steps;

import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.starter.task.Property;
import io.vlingo.xoom.starter.task.TaskExecutionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

import static io.vlingo.xoom.codegen.parameter.Label.*;

public class CodeGenerationParametersMapperTest {

    @Test
    public void testCodeGenerationContextMapping() {
        final TaskExecutionContext taskExecutionContext =
                TaskExecutionContext.withoutOptions();

        loadProperties(taskExecutionContext);

        final Map<Label, String> parameters =
                CodeGenerationParametersMapper.of(taskExecutionContext);

        Assertions.assertEquals("FirstAggregate;SecondAggregate", parameters.get(AGGREGATES));
        Assertions.assertEquals("true", parameters.get(USE_ANNOTATIONS));
        Assertions.assertEquals("xoom-app-name", parameters.get(APPLICATION_NAME));
        Assertions.assertEquals("POSTGRES", parameters.get(COMMAND_MODEL_DATABASE));
        Assertions.assertEquals("false", parameters.get(CQRS));
        Assertions.assertEquals("HSQLDB", parameters.get(DATABASE));
        Assertions.assertEquals("io.vlingo.xoom", parameters.get(PACKAGE));
        Assertions.assertEquals("CUSTOM", parameters.get(PROJECTION_TYPE));
        Assertions.assertEquals("IN_MEMORY", parameters.get(QUERY_MODEL_DATABASE));
        Assertions.assertEquals("true", parameters.get(USE_AUTO_DISPATCH));
        Assertions.assertEquals("FirstAggregate;SecondAggregate", parameters.get(REST_RESOURCES));
        Assertions.assertEquals("STATE_STORE", parameters.get(STORAGE_TYPE));
        Assertions.assertEquals("/home/projects", parameters.get(TARGET_FOLDER));
    }

    private void loadProperties(final TaskExecutionContext context) {
        final Properties properties = new Properties();
        properties.put(Property.AGGREGATES.literal(), "FirstAggregate;SecondAggregate");
        properties.put(Property.ANNOTATIONS.literal(), "true");
        properties.put(Property.ARTIFACT_ID.literal(), "xoom-app-name");
        properties.put(Property.COMMAND_MODEL_DATABASE.literal(), "POSTGRES");
        properties.put(Property.CQRS.literal(), "false");
        properties.put(Property.DATABASE.literal(), "HSQLDB");
        properties.put(Property.PACKAGE.literal(), "io.vlingo.xoom");
        properties.put(Property.PROJECTIONS.literal(), "CUSTOM");
        properties.put(Property.QUERY_MODEL_DATABASE.literal(), "IN_MEMORY");
        properties.put(Property.REST_RESOURCES.literal(), "FirstAggregate;SecondAggregate");
        properties.put(Property.AUTO_DISPATCH.literal(), "true");
        properties.put(Property.STORAGE_TYPE.literal(), "STATE_STORE");
        properties.put(Property.TARGET_FOLDER.literal(), "/home/projects");
        context.onProperties(properties);
    }

}