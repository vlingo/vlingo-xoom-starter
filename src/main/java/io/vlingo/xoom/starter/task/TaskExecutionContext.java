// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.starter.task;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameters;
import io.vlingo.xoom.starter.task.option.OptionName;
import io.vlingo.xoom.starter.task.option.OptionValue;
import io.vlingo.xoom.starter.task.projectgeneration.steps.DeploymentType;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static io.vlingo.xoom.starter.task.Property.*;

public class TaskExecutionContext {

    private Process process;
    private String[] commands;
    private Properties properties;
    private final CodeGenerationParameters parameters;
    private final List<String> args = new ArrayList<>();
    private final List<OptionValue> optionValues = new ArrayList<>();
    private final Map<String, String> configuration = new HashMap<>();
    private final Agent agent;

    public static TaskExecutionContext executedFrom(final Agent agent) {
        return new TaskExecutionContext(agent);
    }

    public static TaskExecutionContext withoutOptions() {
        return new TaskExecutionContext(Agent.TERMINAL);
    }

    private TaskExecutionContext(final Agent agent) {
        this.agent = agent;
        this.parameters = CodeGenerationParameters.empty();
    }

    public void followProcess(final Process process) {
        this.process = process;
    }

    public TaskExecutionContext withOptions(final List<OptionValue> optionValues) {
        this.optionValues.addAll(optionValues);
        return this;
    }

    public TaskExecutionContext withArgs(final List<String> args) {
        this.addArgs(args);
        return this;
    }

    public TaskExecutionContext with(final CodeGenerationParameters parameters) {
        this.parameters.addAll(parameters);
        return this;
    }

    public TaskExecutionContext onConfiguration(final Map<String, String> configurations) {
        this.configuration.putAll(configurations);
        return this;
    }

    public TaskExecutionContext onProperties(final Properties properties) {
        this.properties = properties;
        return this;
    }

    public void withCommands(final String[] commands) {
        this.commands = commands;
    }

    public void addArgs(final List<String> args) {
        this.args.addAll(args);
    }

    public Process process() {
        return process;
    }

    public Properties properties() {
        return properties;
    }

    public Agent agent() {
        return agent;
    }

    public String configurationOf(final String key) {
        return this.configuration.get(key);
    }

    public TaskExecutionContext addProperty(final Property property, final String value) {
        this.properties.put(property.literal(), value);
        return this;
    }

    public <T> T propertyOf(final Property property) {
        return (T) propertyOf(property, value -> value);
    }

    public <T> T propertyOf(final Property property, final Function<String, T> mapper) {
        return this.propertyOf(property.literal(), mapper);
    }

    public <T> T propertyOf(final String propertyValue, final Function<String, T> mapper) {
        final String value = properties.getProperty(propertyValue);
        return (T) mapper.apply(value);
    }

    public boolean hasProperty(final Property property) {
        return this.propertyOf(property) != null && !this.<String>propertyOf(property).trim().isEmpty();
    }

    public String optionValueOf(final OptionName optionName) {
        return optionValues.stream()
                .filter(optionValue -> optionValue.hasName(optionName))
                .map(optionValue -> optionValue.value()).findFirst().get();
    }

    public String[] commands() {
        return commands;
    }

    public List<String> args() {
        return args;
    }

    public CodeGenerationParameters codeGenerationParameters() {
        return parameters;
    }

    public DeploymentType deploymentType() {
        return DeploymentType.valueOf(propertyOf(DEPLOYMENT));
    }

    public String projectPath() {
        final String artifactId = propertyOf(ARTIFACT_ID);
        final String targetFolder = propertyOf(TARGET_FOLDER);
        return Paths.get(targetFolder, artifactId).toString();
    }

    public boolean hasCodeGenerationParameters() {
        return parameters.isEmpty();
    }

}
