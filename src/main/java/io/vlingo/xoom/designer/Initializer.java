// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.designer;

import io.vlingo.xoom.designer.task.TaskExecutor;

import java.util.Arrays;

public class Initializer {

    public static void main(final String[] args) {
        try {
            TaskExecutor.execute(Arrays.asList(args));
        } catch (final Exception exception) {
            exception.printStackTrace();
            System.out.println(exception.getMessage());
        } finally {
            if(TaskExecutor.shouldExit()) {
                System.exit(0);
            }
        }
    }

}
