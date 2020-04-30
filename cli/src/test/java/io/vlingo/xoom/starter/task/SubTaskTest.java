package io.vlingo.xoom.starter.task;

import io.vlingo.xoom.starter.task.option.OptionValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.vlingo.xoom.starter.task.option.OptionName.CURRENT_DIRECTORY;
import static io.vlingo.xoom.starter.task.option.OptionName.TAG;

public class SubTaskTest {

    @Test
    public void testDockerPackageOptionValueRetrieval() {
        final List<String> args =
                Arrays.asList("docker", "package", "--tag", "1.0.0", "--currentDirectory", "/home/starter-project");

        final List<OptionValue> optionValues =
                SubTask.DOCKER_PACKAGE.findOptionValues(args);

        final Optional<OptionValue> tag =
                optionValues.stream().filter(optionValue -> {
                    return optionValue.hasName(TAG);
                }).findFirst();

        final Optional<OptionValue> currentDirectory =
                optionValues.stream().filter(optionValue -> {
                    return optionValue.hasName(CURRENT_DIRECTORY);
                }).findFirst();

        Assertions.assertEquals(2, optionValues.size());
        Assertions.assertEquals("1.0.0", tag.get().value());
        Assertions.assertEquals("/home/starter-project", currentDirectory.get().value());
    }

}
