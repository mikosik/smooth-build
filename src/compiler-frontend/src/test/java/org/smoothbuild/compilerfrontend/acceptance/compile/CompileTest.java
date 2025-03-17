package org.smoothbuild.compilerfrontend.acceptance.compile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.testing.GoldenFilesArgumentsProvider;
import org.smoothbuild.common.testing.GoldenFilesTestCase;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;

/**
 * Test verifying that compilation of given smooth file results in expected log files.
 */
public class CompileTest extends FrontendCompileTester {
  private static final String TESTS_DIR =
      "src/test/java/org/smoothbuild/compilerfrontend/acceptance/compile";

  @ParameterizedTest
  @ArgumentsSource(ArgumentsProvider.class)
  void test_compile(GoldenFilesTestCase testCase) throws IOException {
    testCase.assertWithGoldenFiles(generateFiles(testCase));
  }

  private Map<String, String> generateFiles(GoldenFilesTestCase testCase) throws IOException {
    var code = testCase.readFile("smooth");
    var importedCode = testCase.readFileMaybe("imported");
    var actualLogs = compile(code, importedCode);
    return Map.of("logs", actualLogs);
  }

  private String compile(String code, Maybe<String> importedModule) {
    var testApi = module(code);
    importedModule.ifPresent(testApi::withImported);
    return testApi.loadModule().logs().toString("\n");
  }

  static class ArgumentsProvider extends GoldenFilesArgumentsProvider {
    public ArgumentsProvider() {
      super(Path.of(TESTS_DIR), "smooth");
    }
  }
}
