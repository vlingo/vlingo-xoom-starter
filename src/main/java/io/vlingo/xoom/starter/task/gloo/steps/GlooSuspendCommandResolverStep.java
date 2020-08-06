// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.starter.task.gloo.steps;

import io.vlingo.xoom.starter.task.TaskExecutionContext;
import io.vlingo.xoom.starter.task.steps.CommandResolverStep;

public class GlooSuspendCommandResolverStep extends CommandResolverStep {

    @Override
    protected String formatCommands(TaskExecutionContext context) {
        return "glooctl uninstall gateway";
    }

}
