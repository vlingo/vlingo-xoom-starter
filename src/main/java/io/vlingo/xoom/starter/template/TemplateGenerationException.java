// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.starter.template;

import io.vlingo.xoom.starter.task.TaskExecutionException;

public class TemplateGenerationException extends TaskExecutionException {

    public TemplateGenerationException(final Exception exception) {
        super(exception);
    }

    public TemplateGenerationException(final String message) {
        super(message);
    }

}
