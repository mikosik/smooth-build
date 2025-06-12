package org.smoothbuild.common.testing;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

/**
 * Searches rootDir recursively and for each directory containing file with fileName creates
 * GoldenFileTestCase.
 */
public class GoldenFilesArgumentsProvider implements ArgumentsProvider {
  private final String inputFileName;
  private final Path rootDir;

  public GoldenFilesArgumentsProvider(Path rootDir, String fileName) {
    this.rootDir = rootDir;
    this.inputFileName = fileName;
  }

  @Override
  public Stream<Arguments> provideArguments(
      ParameterDeclarations parameterDeclarations, ExtensionContext context) throws Exception {
    try (Stream<Path> filesStream = Files.walk(rootDir)) {
      var testCases = filesStream
          .filter(Files::isRegularFile)
          .filter(this::isInputFile)
          .map(this::createTestArguments)
          .toList();
      if (testCases.isEmpty()) {
        throw new RuntimeException("Couldn't find any tests in dir `" + rootDir + "`.");
      }
      return testCases.stream();
    }
  }

  private Arguments createTestArguments(Path inputFilePath) {
    var testRootDir = inputFilePath.getParent();
    var testName = testRootDir.toString().replace(rootDir.toString(), "");
    return Arguments.arguments(new GoldenFilesTestCase(testRootDir, testName));
  }

  private boolean isInputFile(Path path) {
    return path.getFileName().toString().equals(inputFileName);
  }
}
