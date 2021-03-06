package io.vlingo.xoom.designer.task.projectgeneration.code.java.unittest.projections;

import io.vlingo.xoom.codegen.CodeGenerationContext;
import io.vlingo.xoom.codegen.TextExpectation;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.dialect.Dialect;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.codegen.template.OutputFile;
import io.vlingo.xoom.designer.task.projectgeneration.Label;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.JavaTemplateStandard;
import io.vlingo.xoom.designer.task.projectgeneration.code.java.projections.ProjectionType;
import io.vlingo.xoom.turbo.OperatingSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static io.vlingo.xoom.designer.task.projectgeneration.Label.METHOD_PARAMETER;

public class ProjectionUnitTestGenerationStepTest {

  private static final String PERSISTENCE_SETUP_CONTENT_TEXT =
          "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                  "public class PersistenceSetup { \\n" +
                  "... \\n" +
                  "}";

  @Test
  public void testThatEventBasedProjectionsUnitTestAreGenerated() throws IOException {
    // GIVEN
    final CodeGenerationParameters parameters = codeGenerationParameters()
            .add(Label.PROJECTION_TYPE, ProjectionType.EVENT_BASED);
    final CodeGenerationContext context =
            CodeGenerationContext.with(parameters).contents(contents());

    // WHEN
    new ProjectionUnitTestGenerationStep().process(context);

    // THEN
    final Content authorProjectionTest =
        context.findContent(JavaTemplateStandard.PROJECTION_UNIT_TEST, "AuthorProjectionTest");

    final Content countingProjectionControl =
            context.findContent(JavaTemplateStandard.COUNTING_PROJECTION_CTL, "CountingProjectionControl");
    final Content countingReadResultInterest =
            context.findContent(JavaTemplateStandard.COUNTING_READ_RESULT, "CountingReadResultInterest");

    Assertions.assertEquals(9, context.contents().size());
    Assertions.assertTrue(countingProjectionControl.contains(TextExpectation.onJava().read("counting-projection-control")));
    Assertions.assertTrue(countingReadResultInterest.contains(TextExpectation.onJava().read("counting-read-result-interest")));
    Assertions.assertTrue(authorProjectionTest.contains(TextExpectation.onJava().read("author-event-based-projection-unit-test")));
  }

  @Test
  public void testThatEntityStateProjectionsUnitTestAreGenerated() throws IOException {
    // GIVEN
    final CodeGenerationParameters parameters = codeGenerationParameters()
            .add(Label.PROJECTION_TYPE, ProjectionType.NONE);
    final CodeGenerationContext context =
            CodeGenerationContext.with(parameters).contents(contents());

    // WHEN
    new ProjectionUnitTestGenerationStep().process(context);

    // THEN
    final Content authorProjectionTest =
            context.findContent(JavaTemplateStandard.PROJECTION_UNIT_TEST, "AuthorProjectionTest");

    Assertions.assertEquals(7, context.contents().size());
    Assertions.assertTrue(authorProjectionTest.contains(TextExpectation.onJava().read("author-entity-state-projection-unit-test")));
  }

  private CodeGenerationParameters codeGenerationParameters() {
    return CodeGenerationParameters.from(Label.PACKAGE, "io.vlingo.xoomapp")
            .add(Label.DIALECT, Dialect.JAVA)
            .add(authorAggregate())
            .add(nameValueObject()).add(rankValueObject())
            .add(tagValueObject())
            .add(classificationValueObject()).add(classifierValueObject());
  }

  private CodeGenerationParameter authorAggregate() {
    final CodeGenerationParameter idField =
        CodeGenerationParameter.of(Label.STATE_FIELD, "id")
            .relate(Label.FIELD_TYPE, "String");

    final CodeGenerationParameter nameField =
        CodeGenerationParameter.of(Label.STATE_FIELD, "name")
            .relate(Label.FIELD_TYPE, "Name");

    final CodeGenerationParameter rankField =
        CodeGenerationParameter.of(Label.STATE_FIELD, "rank")
            .relate(Label.FIELD_TYPE, "Rank")
            .relate(Label.COLLECTION_TYPE, "List");

    final CodeGenerationParameter tagsField =
        CodeGenerationParameter.of(Label.STATE_FIELD, "tags")
            .relate(Label.FIELD_TYPE, "Tag")
            .relate(Label.COLLECTION_TYPE, "List");

    final CodeGenerationParameter relatedAuthorsField =
        CodeGenerationParameter.of(Label.STATE_FIELD, "relatedAuthors")
            .relate(Label.FIELD_TYPE, "String")
            .relate(Label.COLLECTION_TYPE, "Set");

    final CodeGenerationParameter availableOnField =
        CodeGenerationParameter.of(Label.STATE_FIELD, "availableOn")
            .relate(Label.FIELD_TYPE, "LocalDate");

    final CodeGenerationParameter authorRegisteredEvent =
        CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRegistered")
            .relate(idField).relate(nameField);

    final CodeGenerationParameter authorRankedEvent =
        CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRanked")
            .relate(idField).relate(rankField);

    final CodeGenerationParameter authorTaggedEvent =
        CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorTagged")
            .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "id"))
            .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "tags"));

    final CodeGenerationParameter authorUntaggedEvent =
        CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorUntagged")
            .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "id"))
            .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "tags"));

    final CodeGenerationParameter authorRelatedEvent =
        CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorRelated")
            .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "id"))
            .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "relatedAuthors"));

    final CodeGenerationParameter authorUnrelatedEvent =
        CodeGenerationParameter.of(Label.DOMAIN_EVENT, "AuthorUnrelated")
            .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "id"))
            .relate(CodeGenerationParameter.of(Label.STATE_FIELD, "relatedAuthors"));

    final CodeGenerationParameter factoryMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "withName")
            .relate(Label.METHOD_PARAMETER, "name")
            .relate(Label.METHOD_PARAMETER, "availableOn")
            .relate(Label.FACTORY_METHOD, "true")
            .relate(authorRegisteredEvent);

    final CodeGenerationParameter rankMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "changeRank")
            .relate(Label.METHOD_PARAMETER, "rank")
            .relate(authorRankedEvent);

    final CodeGenerationParameter addTagMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "addTag")
            .relate(CodeGenerationParameter.of(METHOD_PARAMETER, "tags")
                .relate(Label.ALIAS, "tag")
                .relate(Label.COLLECTION_MUTATION, "ADDITION"))
            .relate(authorRelatedEvent);

    final CodeGenerationParameter addTagsMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "addTags")
            .relate(CodeGenerationParameter.of(METHOD_PARAMETER, "tags")
                .relate(Label.ALIAS, "")
                .relate(Label.COLLECTION_MUTATION, "MERGE"))
            .relate(authorRelatedEvent);

    final CodeGenerationParameter replaceTagsMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "replaceTags")
            .relate(CodeGenerationParameter.of(METHOD_PARAMETER, "tags")
                .relate(Label.ALIAS, "")
                .relate(Label.COLLECTION_MUTATION, "REPLACEMENT"))
            .relate(authorRelatedEvent);

    final CodeGenerationParameter removeTagMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "removeTag")
            .relate(CodeGenerationParameter.of(METHOD_PARAMETER, "tags")
                .relate(Label.ALIAS, "tag")
                .relate(Label.COLLECTION_MUTATION, "REMOVAL"))
            .relate(authorUnrelatedEvent);

    final CodeGenerationParameter relatedAuthorMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "relateAuthor")
            .relate(CodeGenerationParameter.of(METHOD_PARAMETER, "relatedAuthors")
                .relate(Label.ALIAS, "relatedAuthor")
                .relate(Label.COLLECTION_MUTATION, "ADDITION"))
            .relate(authorRelatedEvent);

    final CodeGenerationParameter relatedAuthorsMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "relateAuthors")
            .relate(CodeGenerationParameter.of(METHOD_PARAMETER, "relatedAuthors")
                .relate(Label.ALIAS, "")
                .relate(Label.COLLECTION_MUTATION, "MERGE"))
            .relate(authorRelatedEvent);

    final CodeGenerationParameter relatedAuthorsReplacementMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "replaceAllRelatedAuthors")
            .relate(CodeGenerationParameter.of(METHOD_PARAMETER, "relatedAuthors")
                .relate(Label.ALIAS, "")
                .relate(Label.COLLECTION_MUTATION, "REPLACEMENT"))
            .relate(authorRelatedEvent);

    final CodeGenerationParameter relatedAuthorRemovalMethod =
        CodeGenerationParameter.of(Label.AGGREGATE_METHOD, "unrelateAuthor")
            .relate(CodeGenerationParameter.of(METHOD_PARAMETER, "relatedAuthors")
                .relate(Label.ALIAS, "relatedAuthor")
                .relate(Label.COLLECTION_MUTATION, "REMOVAL"))
            .relate(authorUnrelatedEvent);
    return CodeGenerationParameter.of(Label.AGGREGATE, "Author").relate(idField)
        .relate(nameField).relate(rankField).relate(availableOnField)
        .relate(tagsField).relate(relatedAuthorsField).relate(factoryMethod)
        .relate(rankMethod)
        .relate(authorRegisteredEvent).relate(authorRankedEvent).relate(addTagMethod)
        .relate(addTagsMethod).relate(replaceTagsMethod).relate(removeTagMethod)
        .relate(relatedAuthorMethod).relate(relatedAuthorsMethod).relate(relatedAuthorRemovalMethod)
        .relate(relatedAuthorsReplacementMethod).relate(authorRegisteredEvent).relate(authorRankedEvent)
        .relate(authorTaggedEvent).relate(authorUntaggedEvent);
  }

  private CodeGenerationParameter nameValueObject() {
    return CodeGenerationParameter.of(Label.VALUE_OBJECT, "Name")
        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "firstName")
            .relate(Label.FIELD_TYPE, "String"))
        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "lastName")
            .relate(Label.FIELD_TYPE, "String"));
  }

  private CodeGenerationParameter tagValueObject() {
    return CodeGenerationParameter.of(Label.VALUE_OBJECT, "Tag")
        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "value")
            .relate(Label.FIELD_TYPE, "String"));
  }

  private CodeGenerationParameter rankValueObject() {
    return CodeGenerationParameter.of(Label.VALUE_OBJECT, "Rank")
        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "points")
            .relate(Label.FIELD_TYPE, "int"))
        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "classification")
            .relate(Label.FIELD_TYPE, "Classification"));
  }

  private CodeGenerationParameter classificationValueObject() {
    return CodeGenerationParameter.of(Label.VALUE_OBJECT, "Classification")
        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "label")
            .relate(Label.FIELD_TYPE, "String"))
        .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "classifiers")
                .relate(Label.FIELD_TYPE, "Classifier").relate(Label.COLLECTION_TYPE, "Set"));
  }

  private CodeGenerationParameter classifierValueObject() {
    return CodeGenerationParameter.of(Label.VALUE_OBJECT, "Classifier")
            .relate(CodeGenerationParameter.of(Label.VALUE_OBJECT_FIELD, "name")
                    .relate(Label.FIELD_TYPE, "String"));

  }

  private static final String PROJECT_PATH =
      OperatingSystem.detect().isWindows() ?
          Paths.get("D:\\projects", "xoom-app").toString() :
          Paths.get("/home", "xoom-app").toString();

  private static final String INFRASTRUCTURE_PACKAGE_PATH =
      Paths.get(PROJECT_PATH, "src", "main", "java",
          "io", "vlingo", "xoomapp", "infrastructure").toString();

  private static final String MODEL_PACKAGE_PATH =
      Paths.get(PROJECT_PATH, "src", "main", "java",
          "io", "vlingo", "xoomapp", "model").toString();
  private static final String PERSISTENCE_PACKAGE_PATH =
      Paths.get(INFRASTRUCTURE_PACKAGE_PATH, "persistence").toString();

  private static final String AUTHOR_DATA_CONTENT_TEXT =
      "package io.vlingo.xoomapp.infrastructure; \\n" +
          "public class AuthorData { \\n" +
          "... \\n" +
          "}";
  private static final String AUTHOR_STATE_CONTENT_TEXT =
      "package io.vlingo.xoomapp.model.author; \\n" +
          "public class AuthorState { \\n" +
          "... \\n" +
          "}";

  private static final String AUTHOR_REGISTERED_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.author; \\n" +
                  "public class AuthorRegistered extends DomainEvent { \\n" +
                  "... \\n" +
                  "}";

  private static final String AUTHOR_RANKED_CONTENT_TEXT =
          "package io.vlingo.xoomapp.model.author; \\n" +
                  "public class AuthorRanked extends DomainEvent { \\n" +
                  "... \\n" +
                  "}";

  private Content[] contents() {
    return new Content[]{
            Content.with(JavaTemplateStandard.AGGREGATE_STATE, new OutputFile(Paths.get(MODEL_PACKAGE_PATH).toString(), "AuthorState.java"), null, null, AUTHOR_STATE_CONTENT_TEXT),
            Content.with(JavaTemplateStandard.DATA_OBJECT, new OutputFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "AuthorData.java"), null, null, AUTHOR_DATA_CONTENT_TEXT),
            Content.with(JavaTemplateStandard.DOMAIN_EVENT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorRegistered.java"), null, null, AUTHOR_REGISTERED_CONTENT_TEXT),
            Content.with(JavaTemplateStandard.DOMAIN_EVENT, new OutputFile(Paths.get(MODEL_PACKAGE_PATH, "author").toString(), "AuthorRanked.java"), null, null, AUTHOR_RANKED_CONTENT_TEXT),
            Content.with(JavaTemplateStandard.PROJECTION, new OutputFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "AuthorProjectionActor.java"), null, null, AUTHOR_PROJECTION_CONTENT_TEXT),
            Content.with(JavaTemplateStandard.PERSISTENCE_SETUP, new OutputFile(Paths.get(PERSISTENCE_PACKAGE_PATH).toString(), "PersistenceSetup.java"), null, null, PERSISTENCE_SETUP_CONTENT_TEXT),

    };
  }

  private static final String AUTHOR_PROJECTION_CONTENT_TEXT =
          "package io.vlingo.xoomapp.infrastructure.persistence; \\n" +
                  "public class AuthorProjectionActor { \\n" +
                  "... \\n" +
                  "}";
}
