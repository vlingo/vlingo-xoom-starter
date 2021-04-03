// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.starter.task.steps;

import io.vlingo.xoom.starter.task.TaskExecutionContext;
import io.vlingo.xoom.starter.terminal.CommandExecutionProcess;
import io.vlingo.xoom.starter.terminal.Terminal;

import java.io.File;
import java.util.Collections;
import java.util.List;

public abstract class CommandExecutionStep implements TaskExecutionStep {

  private final CommandExecutionProcess commandExecutionProcess;

  protected CommandExecutionStep(final CommandExecutionProcess commandExecutionProcess) {
    this.commandExecutionProcess = commandExecutionProcess;
  }

  public void process(final TaskExecutionContext context) {
    grantPermissions();
    final String commands = formatCommands(context);
    System.out.println("Executing commands from " + this.getClass().getCanonicalName());
    commandExecutionProcess.handle(commands);
  }

  protected abstract String formatCommands(final TaskExecutionContext context);

  protected List<File> executableFiles() {
    return Collections.emptyList();
  }

  protected void grantPermissions() {
    executableFiles().forEach(file -> Terminal.grantAllPermissions(file));
  }


}
