package org.smoothbuild.compilerfrontend.acceptance.infer;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.log.base.Try;
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
  @ArgumentsSource(FileArgumentsProvider.class)
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
    return Path.of(input.toString() + extension);
  }

  static class FileArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) throws Exception {
      var dir = Paths.get(TESTS_DIR);
      try (Stream<Path> filesStream = Files.walk(dir)) {
        var testCases = filesStream
            .filter(Files::isRegularFile)
            .filter(FileArgumentsProvider::isInputFile)
            .map(this::createTestArguments)
            .toList();
        if (testCases.isEmpty()) {
          throw new RuntimeException("Couldn't find any tests in dir `" + dir + "`.");
        }
        return testCases.stream();
      }
    }

    private Arguments createTestArguments(Path inputPath) {
      var testName = inputPath.toString().replace(TESTS_DIR, "");
      return arguments(testName, inputPath);
    }

    private static boolean isInputFile(Path path) {
      return path.getFileName().toString().endsWith(".smooth");
    }
  }
}
