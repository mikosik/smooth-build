package org.smoothbuild.compilerfrontend.acceptance;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class TestFileArgumentsProvider implements ArgumentsProvider {
  private final Path testRootDir;

  public TestFileArgumentsProvider(Path testRootDir) {
    this.testRootDir = testRootDir;
  }

  @Override
  public Stream<Arguments> provideArguments(ExtensionContext context) throws Exception {
    try (Stream<Path> filesStream = Files.walk(testRootDir)) {
      var testCases = filesStream
          .filter(Files::isRegularFile)
          .filter(TestFileArgumentsProvider::isInputFile)
          .map(this::createTestArguments)
          .toList();
      if (testCases.isEmpty()) {
        throw new RuntimeException("Couldn't find any tests in dir `" + testRootDir + "`.");
      }
      return testCases.stream();
    }
  }

  private Arguments createTestArguments(Path inputPath) {
    var testName = inputPath.toString().replace(testRootDir.toString(), "");
    return arguments(testName, inputPath);
  }

  private static boolean isInputFile(Path path) {
    return path.getFileName().toString().endsWith(".smooth");
  }
}
