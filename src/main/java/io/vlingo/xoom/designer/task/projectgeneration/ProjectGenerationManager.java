// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.designer.task.projectgeneration;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.designer.Configuration;
import io.vlingo.xoom.designer.infrastructure.restapi.data.GenerationSettingsData;
import io.vlingo.xoom.designer.infrastructure.restapi.data.TaskExecutionContextMapper;
import io.vlingo.xoom.designer.infrastructure.restapi.report.ProjectGenerationReport;
import io.vlingo.xoom.designer.task.TaskExecutionContext;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.schemata.SchemaPullException;

import java.io.File;

import static io.vlingo.xoom.designer.infrastructure.restapi.report.ProjectGenerationReport.*;
import static io.vlingo.xoom.designer.task.TaskOutput.PROJECT_GENERATION_REPORT;

public class ProjectGenerationManager {

  public Completes<TaskExecutionContext> generate(final GenerationSettingsData settings,
                                                  final ProjectGenerationInformation information) {
    return validate(settings, information)
            .andThenTo(context -> mapContext(settings, information.generationTarget))
            .andThenConsume(context -> processSteps(context, information));
  }

  private Completes<TaskExecutionContext> mapContext(final GenerationSettingsData settings,
                                                     final GenerationTarget target) {
    try {
      return Completes.withSuccess(TaskExecutionContextMapper.map(settings, target));
    } catch (final Exception exception) {
      exception.printStackTrace();
      final ProjectGenerationReport report = onContextMappingFail(target, settings, exception);
      return Completes.withFailure(TaskExecutionContext.withOutput(PROJECT_GENERATION_REPORT, report));
    }
  }

  private Completes<TaskExecutionContext> validate(final GenerationSettingsData settings, ProjectGenerationInformation information) {
    final String validationErrors = String.join(", ", settings.validate());
    if(validationErrors.isEmpty()) {
      return Completes.withSuccess(TaskExecutionContext.empty());
    }
    final ProjectGenerationReport report = onValidationFail(validationErrors, information.generationTarget);
    return Completes.withFailure(TaskExecutionContext.withOutput(PROJECT_GENERATION_REPORT, report));
  }

  public void createGenerationPath(final File generationPath) {
    try {
      if (generationPath.exists() && generationPath.isDirectory() && generationPath.list().length > 0) {
        throw new GenerationPathAlreadyExistsException();
      }
      generationPath.mkdirs();
    } catch (final Exception e) {
      e.printStackTrace();
      throw new GenerationPathCreationException();
    }
  }

  private void processSteps(final TaskExecutionContext context, final ProjectGenerationInformation information) {
    try {
      Configuration.PROJECT_GENERATION_STEPS.stream()
              .filter(step -> step.shouldProcess(context))
              .forEach(step -> step.process(context));

      context.addOutput(PROJECT_GENERATION_REPORT, onSuccess(context, information));
    } catch (final SchemaPullException exception) {
      exception.printStackTrace();
      context.addOutput(PROJECT_GENERATION_REPORT, onSchemaPullFail(information));
    } catch (final Exception exception) {
      exception.printStackTrace();
      context.addOutput(PROJECT_GENERATION_REPORT, onFail(context, information, exception));
    }
  }

}
