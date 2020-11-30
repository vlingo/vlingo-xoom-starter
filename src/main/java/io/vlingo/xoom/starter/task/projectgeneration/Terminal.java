// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.starter.task.projectgeneration;

import java.util.Arrays;
import java.util.function.Predicate;

public enum Terminal {

    WINDOWS("cmd.exe", "/c", "MOVE /Y", osName -> osName.contains("Windows")),
    MAC_OS("sh", "-c", "mv -f", osName -> osName.toUpperCase().contains("MAC OS")),
    LINUX("sh", "-c", "mv -f", osName -> osName.contains("Linux"));

    private static Terminal ENABLED;

    private final String initializationCommand;
    private final String parameter;
    private final String mavenCommand;
    private final Predicate<String> activationCondition;

    Terminal(final String initializationCommand,
             final String parameter,
             final String mavenCommand,
             final Predicate<String> activationCondition) {
        this.initializationCommand = initializationCommand;
        this.parameter = parameter;
        this.mavenCommand = mavenCommand;
        this.activationCondition = activationCondition;
    }

    public static Terminal supported() {
        if(ENABLED != null) {
            return ENABLED;
        }
        return Arrays.asList(Terminal.values()).stream()
                .filter(terminal -> terminal.isSupported())
                .findFirst().get();
    }

    public static void enable(final Terminal terminal) {
        ENABLED = terminal;
    }

    public static void disable() {
        ENABLED = null;
    }

    public String initializationCommand() {
        return initializationCommand;
    }

    public String parameter() {
        return parameter;
    }

    private boolean isSupported() {
        return activationCondition.test(System.getProperty("os.name"));
    }

    public boolean isWindows() {
        return this.equals(WINDOWS);
    }

    public String mavenCommand() {
        return mavenCommand;
    }

}
