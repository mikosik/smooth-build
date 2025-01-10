package org.smoothbuild.compilerfrontend.acceptance.compile;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.compilerfrontend.acceptance.TestFileArgumentsProvider;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;

/**
 * Test verifying that compilation of given smooth file results in expected log files.
 * To overwrite files with expected output with actual output, change field MODE to OVERWRITE.
 */
public class CompileTest extends FrontendCompileTester {
  private static final Mode MODE = Mode.ASSERT;
  private static final String TESTS_DIR =
      "src/test/java/org/smoothbuild/compilerfrontend/acceptance/compile";

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
    var actualLogs = compile(input);
    var expectedLogs = Files.readString(withExtension(input, ".logs"));
    assertThat(actualLogs).isEqualTo(expectedLogs);
  }

  private void overwriteTest(Path input) throws IOException {
    var logs = compile(input);
    Files.writeString(withExtension(input, ".logs"), logs);
  }

  private String compile(Path input) throws IOException {
    var code = Files.readString(input);
    var testApi = module(code);
    var importedPath = withSuffix(input, "-imported");
    if (Files.exists(importedPath)) {
      testApi.withImported(Files.readString(importedPath));
    }
    return testApi.loadModule().logs().toString("\n");
  }

  private static Path withExtension(Path input, String extension) {
    var name = input.getFileName().toString();
    var dotIndex = name.lastIndexOf(".");
    var newName = dotIndex == -1 ? name + extension : name.substring(0, dotIndex) + extension;
    return input.getParent().resolve(newName);
  }

  private static Path withSuffix(Path input, String suffix) {
    var name = input.getFileName().toString();
    var dotIndex = name.lastIndexOf(".");
    var newName = dotIndex == -1
        ? name + suffix
        : name.substring(0, dotIndex) + suffix + name.substring(dotIndex + 1);
    return input.getParent().resolve(newName);
  }

  static class ArgumentsProvider extends TestFileArgumentsProvider {
    public ArgumentsProvider() {
      super(Path.of(TESTS_DIR));
    }
  }
}
