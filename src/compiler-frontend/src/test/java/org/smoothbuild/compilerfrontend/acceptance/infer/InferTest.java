package org.smoothbuild.compilerfrontend.acceptance.infer;

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
 * Test verifying that types that are not declared explicitly are inferred correctly.
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
    var module = loadModule(input);
    var expectedPath = withExtension(input, ".expected");
    if (Files.exists(expectedPath)) {
      assertThat(module.get().toSourceCode()).isEqualTo(Files.readString(expectedPath));
    }
    var logsPath = withExtension(input, ".logs");
    if (Files.exists(logsPath)) {
      assertThat(module.logs().toString("\n")).isEqualTo(Files.readString(logsPath));
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
