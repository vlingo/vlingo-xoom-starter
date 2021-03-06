// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.designer.task.projectgeneration.code.java.schemata;

import io.vlingo.xoom.codegen.content.CodeElementFormatter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.designer.task.projectgeneration.Label;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.model.FieldDetail;

import java.util.function.Function;

import static io.vlingo.xoom.designer.task.projectgeneration.CodeGenerationProperties.DEFAULT_SCHEMA_VERSION;

public class Schema {

  public final String reference;
  public final String file;

  public Schema(final String schemaReference) {
    this.reference = schemaReference;
    this.file = null;
  }

  public Schema(final String schemaGroup,
                final CodeGenerationParameter publishedLanguage) {
    if (!(publishedLanguage.isLabeled(Label.DOMAIN_EVENT) || publishedLanguage.isLabeled(Label.VALUE_OBJECT))) {
      throw new IllegalArgumentException("A Domain Event or Value Object parameter is expected.");
    }
    this.reference = String.format("%s:%s:%s", schemaGroup, publishedLanguage.value, DEFAULT_SCHEMA_VERSION);
    this.file = publishedLanguage.value + ".vss";
  }

  public String simpleClassName() {
    return reference.split(":")[3];
  }

  public String qualifiedName() {
    final String packageName =  reference.split(":")[2] + ".event";
    return CodeElementFormatter.qualifiedNameOf(packageName, simpleClassName());
  }

  public String innerReceiverClassName() {
    return simpleClassName() + "Receiver";
  }

  static String resolveFieldDeclaration(final CodeGenerationParameter field) {
    final String fieldType = field.retrieveRelatedValue(Label.FIELD_TYPE);
    final Function<String, String> schemaFieldTypeConverter =
            type -> FieldDetail.isDateTime(type) ? "timestamp" : fieldType;

    if (FieldDetail.isAssignableToValueObject(field)) {
      return String.format("data.%s:%s %s", fieldType, DEFAULT_SCHEMA_VERSION, field.value);
    }

    if (FieldDetail.isCollection(field)) {
      final String genericType = FieldDetail.genericTypeOf(field.parent(), field.value);
      if (FieldDetail.isScalar(genericType)) {
        return String.format("%s[] %s", genericType, field.value);
      } else {
        return String.format("data.%s:%s[] %s", schemaFieldTypeConverter.apply(genericType), DEFAULT_SCHEMA_VERSION, field.value);
      }
    }

    return schemaFieldTypeConverter.apply(fieldType).toLowerCase() + " " + field.value;
  }

}
