// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.designer.task.projectgeneration.code.java.model.aggregate;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.JavaTemplateStandard;
import io.vlingo.xoom.designer.task.projectgeneration.Label;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.model.valueobject.ValueObjectDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.vlingo.xoom.designer.task.projectgeneration.code.java.TemplateParameter.*;

public class AggregateProtocolTemplateData extends TemplateData {

  private final String protocolName;
  private final TemplateParameters parameters;

  @SuppressWarnings("unchecked")
  public AggregateProtocolTemplateData(final String packageName,
                                       final CodeGenerationParameter aggregate,
                                       final List<Content> contents,
                                       final Boolean useCQRS) {
    this.protocolName = aggregate.value;
    this.parameters = TemplateParameters.with(PACKAGE_NAME, packageName)
            .addImports(resolveImports(aggregate, contents))
            .and(AGGREGATE_PROTOCOL_NAME, aggregate.value)
            .and(STATE_NAME, JavaTemplateStandard.AGGREGATE_STATE.resolveClassname(aggregate.value))
            .and(METHODS, new ArrayList<String>())
            .and(USE_CQRS, useCQRS);

    this.dependOn(AggregateProtocolMethodTemplateData.from(parameters, aggregate));
  }

  @Override
  public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
    this.parameters.<List<String>>find(METHODS).add(outcome);
  }

  private Set<String> resolveImports(final CodeGenerationParameter aggregate, final List<Content> contents) {
    return ValueObjectDetail.resolveImports(contents, aggregate.retrieveAllRelated(Label.STATE_FIELD));
  }

  @Override
  public String filename() {
    return standard().resolveFilename(protocolName, parameters);
  }

  @Override
  public TemplateParameters parameters() {
    return parameters;
  }

  @Override
  public TemplateStandard standard() {
    return JavaTemplateStandard.AGGREGATE_PROTOCOL;
  }

}
