// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.designer.task.projectgeneration.code.java.formatting;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.designer.task.projectgeneration.Label;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.model.FieldDetail;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.model.MethodScope;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.model.aggregate.AggregateDetail;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.model.domainevent.DomainEventDetail;

import java.util.stream.Collectors;

import static io.vlingo.xoom.designer.task.projectgeneration.Label.ALIAS;

public class SourcedStateMethodInvocation implements Formatters.Arguments {

  @Override
  public String format(final CodeGenerationParameter method, final MethodScope scope) {
    final String eventName = method.retrieveRelatedValue(Label.DOMAIN_EVENT);
    final CodeGenerationParameter aggregate = method.parent(Label.AGGREGATE);
    final CodeGenerationParameter domainEvent = AggregateDetail.eventWithName(aggregate, eventName);
    return method.retrieveAllRelated(Label.METHOD_PARAMETER)
            .map(parameter -> resolveFieldAccess(aggregate, domainEvent, parameter))
            .collect(Collectors.joining(", "));
  }

  private String resolveFieldAccess(final CodeGenerationParameter aggregate,
                                    final CodeGenerationParameter domainEvent,
                                    final CodeGenerationParameter methodParameter) {
    if (DomainEventDetail.hasField(domainEvent, methodParameter.value)) {
      return "event." + (methodParameter.hasAny(ALIAS) ? methodParameter.retrieveRelatedValue(ALIAS) : methodParameter.value);
    }
    return FieldDetail.resolveDefaultValue(aggregate, methodParameter.value);
  }
}
