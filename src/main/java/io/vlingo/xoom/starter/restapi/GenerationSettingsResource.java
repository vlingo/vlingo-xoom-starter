// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.starter.restapi;

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.http.ResponseHeader;
import io.vlingo.http.resource.DynamicResourceHandler;
import io.vlingo.http.resource.Resource;
import io.vlingo.xoom.starter.restapi.data.GenerationSettingsData;
import io.vlingo.xoom.starter.restapi.data.TaskExecutionContextMapper;
import io.vlingo.xoom.starter.task.Task;
import io.vlingo.xoom.starter.task.TaskExecutionContext;
import io.vlingo.xoom.starter.task.TaskStatus;

import java.util.List;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.resource.ResourceBuilder.post;
import static io.vlingo.http.resource.ResourceBuilder.resource;
import static io.vlingo.xoom.starter.task.Task.WEB_BASED_PROJECT_GENERATION;

public class GenerationSettingsResource extends DynamicResourceHandler {

    public GenerationSettingsResource(final Stage stage) {
        super(stage);
    }

    public Completes<Response> startGeneration(final GenerationSettingsData settings) {
        final String validationMessage = validate(settings);

        if(validationMessage.length() > 0) {
            logger().debug(validationMessage);
            return Completes.withFailure(Response.of(Conflict, serialized(validationMessage)));
        }

        return mapContext(settings).andThen(this::runProjectGeneration).andThenTo(this::buildResponse);
    }

    private Completes<TaskExecutionContext> mapContext(final GenerationSettingsData settings) {
        try {
            return Completes.withSuccess(TaskExecutionContextMapper.from(settings));
        } catch (final Exception exception) {
            exception.printStackTrace();
            return Completes.withFailure(TaskExecutionContext.withoutOptions());
        }
    }

    private TaskStatus runProjectGeneration(final TaskExecutionContext context) {
        try {
            return Task.of(WEB_BASED_PROJECT_GENERATION, context).manage(context);
        } catch (final Exception exception) {
            exception.printStackTrace();
            return TaskStatus.FAILED;
        }
    }

    private Completes<Response> buildResponse(final TaskStatus taskStatus) {
        final Response.Status status = taskStatus.failed() ? InternalServerError : Ok;
        return Completes.withSuccess(Response.of(status, serialized(taskStatus)));
    }

    private String validate(final GenerationSettingsData settings) {
        final List<String> errorStrings = settings.validate();
        logger().debug("errorStrings: " + errorStrings);
        return String.join(", ", errorStrings);
    }

    @Override
    public Resource<?> routes() {
        return resource("Generation Settings Resource",
                post("/api/generation-settings")
                    .body(GenerationSettingsData.class)
                    .handle(this::startGeneration));
    }

}
