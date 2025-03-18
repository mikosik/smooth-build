package org.smoothbuild.systemtest.cli.command.help;

import static org.smoothbuild.common.base.Strings.convertOsLineSeparatorsToNewLine;

import com.google.common.base.Splitter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.testing.GoldenFilesArgumentsProvider;
import org.smoothbuild.common.testing.GoldenFilesTestCase;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestContext;

public class HelpCommandTest extends SystemTestContext {
  private static final Path TESTS_DIR =
      Path.of("src/test/java/org/smoothbuild/systemtest/cli/command/help");

  @ParameterizedTest
  @ArgumentsSource(ArgumentsProvider.class)
  void test_help(GoldenFilesTestCase testCase) throws IOException {
    var command =
        Splitter.on(' ').splitToList(testCase.readFile("command")).toArray(new String[] {});
    runSmoothWithoutProjectAndInstallationDir(new CommandWithArgs(command));
    assertFinishedWithSuccess();
    var output = convertOsLineSeparatorsToNewLine(systemOut());

    testCase.assertWithGoldenFiles(Map.of("output", output));
  }

  static class ArgumentsProvider extends GoldenFilesArgumentsProvider {
    public ArgumentsProvider() {
      super(TESTS_DIR, "command");
    }
  }
}
