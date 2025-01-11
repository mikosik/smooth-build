package org.smoothbuild.compilerfrontend.acceptance.infer;

import static com.google.common.truth.Truth.assertWithMessage;

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
 * Test verifying that type that is not declared explicitly is inferred correctly or error is
 * reported when it is impossible to infer it.
 * To overwrite files with expected output with actual output, change field MODE to OVERWRITE.
 */
public class InferTest extends FrontendCompileTester {
  private static final Mode MODE = Mode.ASSERT;
  private static final String TESTS_DIR =
      "src/test/java/org/smoothbuild/compilerfrontend/acceptance/infer";

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
    var code = Files.readString(input);
    var module = loadModule(code);
    var expectedPath = withExtension(input, ".expected");
    var inputs =
        """
        **** code ****
        %s
        **** end ****
        """.formatted(code);
    if (Files.exists(expectedPath)) {
      assertWithMessage(inputs)
          .that(module.get().toSourceCode())
          .isEqualTo(Files.readString(expectedPath));
    }
    var logsPath = withExtension(input, ".logs");
    if (Files.exists(logsPath)) {
      assertWithMessage(inputs)
          .that(module.logs().toString("\n"))
          .isEqualTo(Files.readString(logsPath));
    }
  }

  private void overwriteTest(Path input) throws IOException {
    var module = loadModule(input);
    module
        .toMaybe()
        .ifPresent(m -> Files.writeString(withExtension(input, ".expected"), m.toSourceCode()));
    var logs = module.logs();
    if (!logs.isEmpty()) {
      Files.writeString(withExtension(input, ".logs"), logs.toString("\n"));
    }
  }

  private Try<SModule> loadModule(Path input) throws IOException {
    return loadModule(Files.readString(input));
  }

  private Try<SModule> loadModule(String code) {
    return module(code).loadModule();
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
