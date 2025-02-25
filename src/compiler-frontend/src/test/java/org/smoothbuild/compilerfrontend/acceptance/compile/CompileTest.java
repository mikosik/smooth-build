package org.smoothbuild.compilerfrontend.acceptance.compile;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.collect.Maybe;
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
  void test_compile(String testName, Path input) throws IOException {
    switch (MODE) {
      case ASSERT -> assertTest(input);
      case OVERWRITE -> overwriteTest(input);
    }
  }

  private void assertTest(Path input) throws IOException {
    var code = Files.readString(input);
    var importedCode = readImportedModule(input).getOr("");
    var actualLogs = compile(input, code);
    var expectedLogs = Files.readString(withExtension(input, ".logs"));
    var inputs =
        """
        **** test ****
        %s
        **** code ****
        %s
        **** imported code ****
        %s
        **** end ****
        """
            .formatted(input, code, importedCode);
    assertWithMessage(inputs).that(actualLogs).isEqualTo(expectedLogs);
  }

  private void overwriteTest(Path input) throws IOException {
    var logs = compile(input);
    Files.writeString(withExtension(input, ".logs"), logs);
  }

  private String compile(Path input) throws IOException {
    return compile(input, Files.readString(input));
  }

  private String compile(Path input, String code) throws IOException {
    var testApi = module(code);
    readImportedModule(input).ifPresent(testApi::withImported);
    return testApi.loadModule().logs().toString("\n");
  }

  private static Maybe<String> readImportedModule(Path input) throws IOException {
    return readFile(withSuffix(input, "-imported"));
  }

  private static Maybe<String> readFile(Path importedPath) throws IOException {
    if (Files.exists(importedPath)) {
      return some(Files.readString(importedPath));
    }
    return none();
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
