package org.smoothbuild.compilerfrontend.acceptance.usage;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.acceptance.TestFileArgumentsProvider;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;

/**
 * Test verifying that given expression can be used in given context.
 * To overwrite files with expected output with actual output, change field MODE to OVERWRITE.
 */
public class UsageTest extends FrontendCompileTester {
  private static final Mode MODE = Mode.ASSERT;
  private static final String TESTS_DIR =
      "src/test/java/org/smoothbuild/compilerfrontend/acceptance/usage";

  enum Mode {
    ASSERT,
    OVERWRITE
  }

  @ParameterizedTest(name = "{0}")
  @ArgumentsSource(ArgumentsProvider.class)
  void test_inference(String testName, Path input) throws IOException {
    switch (MODE) {
      case ASSERT -> assertTest(input);
      case OVERWRITE -> overwriteTest(input);
    }
  }

  private void assertTest(Path input) throws IOException {
    var actualLogs = loadModule(input).logs().toString("\n");
    var expectedLogs = Files.readString(withExtension(input, ".logs"));
    assertThat(actualLogs).isEqualTo(expectedLogs);
  }

  private void overwriteTest(Path input) throws IOException {
    Files.writeString(withExtension(input, ".logs"), loadModule(input).logs().toString("\n"));
  }

  private Try<SModule> loadModule(Path input) throws IOException {
    return module(Files.readString(input)).loadModule();
  }

  private static Path withExtension(Path input, String extension) {
    var name = input.getFileName().toString();
    var dotIndex = name.lastIndexOf(".");
    var newName = dotIndex == -1 ? name + extension : name.substring(0, dotIndex) + extension;
    return input.getParent().resolve(newName);
  }

  static class ArgumentsProvider extends TestFileArgumentsProvider {
    public ArgumentsProvider() {
      super(Path.of(TESTS_DIR));
    }
  }
}
