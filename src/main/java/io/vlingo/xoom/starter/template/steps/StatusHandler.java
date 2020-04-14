// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.starter.template.steps;

import io.vlingo.xoom.starter.template.archetype.ArchetypeProperties;
import io.vlingo.xoom.starter.template.TemplateGenerationException;
import io.vlingo.xoom.starter.task.TaskExecutionContext;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class StatusHandler {

    private static final String FAILURE_MESSAGE = "vlingo/xoom template generation failed.";
    private static final String SUCCESS_MESSAGE = "vlingo/xoom template has been successfully generated under %s.";
    private static final List<StatusHandler> HANDLERS =
            Arrays.asList(StatusHandler.successHandling(), StatusHandler.failureHandling());

    private final Predicate<Integer> predicate;
    private final Consumer<TaskExecutionContext> handler;

    private StatusHandler(Predicate<Integer> predicate, Consumer<TaskExecutionContext> handler) {
        this.predicate = predicate;
        this.handler = handler;
    }

    public static StatusHandler forStatus(final Integer status) {
        return HANDLERS.stream().filter(handler -> handler.canHandle(status)).findFirst().get();
    }

    public boolean canHandle(final int status) {
        return predicate.test(status);
    }

    public void handle(final TaskExecutionContext context) {
        handler.accept(context);
    }

    private static StatusHandler successHandling() {
        return new StatusHandler(
                status -> status == 0,
                context -> {
                    System.out.println(String.format(SUCCESS_MESSAGE, context.propertyOf(ArchetypeProperties.TARGET_FOLDER)));
                });
    }

    private static StatusHandler failureHandling() {
        return new StatusHandler(
                status -> status != 0,
                context -> {
                    throw new TemplateGenerationException(FAILURE_MESSAGE);
                });
    }

}
