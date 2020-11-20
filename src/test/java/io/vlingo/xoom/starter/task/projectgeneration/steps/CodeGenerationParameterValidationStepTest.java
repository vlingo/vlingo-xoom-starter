package io.vlingo.xoom.starter.task.projectgeneration.steps;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import io.vlingo.xoom.starter.restapi.data.APIData;
import io.vlingo.xoom.starter.restapi.data.AggregateData;
import io.vlingo.xoom.starter.restapi.data.AggregateMethodData;
import io.vlingo.xoom.starter.restapi.data.ContextSettingsData;
import io.vlingo.xoom.starter.restapi.data.DeploymentSettingsData;
import io.vlingo.xoom.starter.restapi.data.DomainEventData;
import io.vlingo.xoom.starter.restapi.data.GenerationSettingsData;
import io.vlingo.xoom.starter.restapi.data.ModelSettingsData;
import io.vlingo.xoom.starter.restapi.data.PersistenceData;
import io.vlingo.xoom.starter.restapi.data.RouteData;
import io.vlingo.xoom.starter.restapi.data.StateFieldData;
import io.vlingo.xoom.starter.restapi.data.TaskExecutionContextMapper;
import io.vlingo.xoom.starter.task.TaskExecutionContext;

public class CodeGenerationParameterValidationStepTest {
	
	@Test
	public void testThatParametersAreValidated() {
		final GenerationSettingsData data =
                new GenerationSettingsData(contextSettingsData(), modelSettingsData(),
						deploymentSettingsData(), "/home/projects", true, false);
						
		TaskExecutionContext context = TaskExecutionContextMapper.from(data);

		assertDoesNotThrow(new Executable() {
			@Override
			public void execute() throws Throwable {
				new CodeGenerationParameterValidationStep().process(context);
			}
		});
	}

	private ContextSettingsData contextSettingsData() {
        return new ContextSettingsData("io.vlingo", "xoomapp",
                "1.0.0", "io.vlingo.xoomapp", "1.3.4-SNAPSHOT");
    }

    private ModelSettingsData modelSettingsData() {
        return new ModelSettingsData(persistenceData(),
                Arrays.asList(personAggregateData(), profileAggregateData()));
    }

    private PersistenceData persistenceData() {
        return new PersistenceData("STATE_STORE", true, "EVENT_BASED",
                "IN_MEMORY", "POSTGRES", "MYSQL");
    }

    private AggregateData personAggregateData() {
        final List<StateFieldData> statesFields =
                Arrays.asList(new StateFieldData("id", "Long"), new StateFieldData("name", "String"));

        final List<AggregateMethodData> methods =
                Arrays.asList(new AggregateMethodData("defineWith", Arrays.asList("name"), true, "PersonDefined"),
                        new AggregateMethodData("changeName", Arrays.asList("name"), false, "PersonNameChanged"));

        final List<DomainEventData> events =
                Arrays.asList(new DomainEventData("PersonDefined", Arrays.asList("id", "name")),
                        new DomainEventData("PersonNameChanged", Arrays.asList("name")));

        final APIData apiData = new APIData("/persons/",
                Arrays.asList(new RouteData("/persons/", "POST", "defineWith", false),
                        new RouteData("/persons/{id}/name", "PATCH", "defineWith", true)));

        return new AggregateData("Person", apiData, events, statesFields, methods);
    }

    private AggregateData profileAggregateData() {
        final List<StateFieldData> statesFields =
                Arrays.asList(new StateFieldData("id", "Long"), new StateFieldData("status", "String"));

        final List<AggregateMethodData> methods =
                Arrays.asList(new AggregateMethodData("publish", Arrays.asList("status"), true, "ProfilePublished"),
                        new AggregateMethodData("changeStatus", Arrays.asList("status"), false, "ProfileStatusChanged"));

        final List<DomainEventData> events =
                Arrays.asList(new DomainEventData("ProfilePublished", Arrays.asList("id", "status")),
                        new DomainEventData("ProfilePublished", Arrays.asList("status")));

        final APIData apiData = new APIData("/profiles/",
                Arrays.asList(new RouteData("/profiles/", "POST", "defineWith", false),
                        new RouteData("/profiles/{id}/status", "PATCH", "defineWith", true)));

        return new AggregateData("Profile", apiData, events, statesFields, methods);
    }

    private DeploymentSettingsData deploymentSettingsData() {
        return new DeploymentSettingsData(0, "DOCKER", "xoom-app", "", "");
    }
}
