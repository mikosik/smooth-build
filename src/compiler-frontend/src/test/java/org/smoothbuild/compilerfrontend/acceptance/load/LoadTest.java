package org.smoothbuild.compilerfrontend.acceptance.load;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.testing.GoldenFilesArgumentsProvider;
import org.smoothbuild.common.testing.GoldenFilesTestCase;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;

/**
 * Tests verifying that compiled smooth code is loaded as expected.
 */
public class LoadTest extends FrontendCompileTester {
  private static final String TESTS_DIR =
      "src/test/java/org/smoothbuild/compilerfrontend/acceptance/load";

  @ParameterizedTest
  @ArgumentsSource(ArgumentsProvider.class)
  void test_compile(GoldenFilesTestCase testCase) throws IOException {
    testCase.assertWithGoldenFiles(generateFiles(testCase));
  }

  private Map<String, String> generateFiles(GoldenFilesTestCase testCase) throws IOException {
    var module = module(testCase.readFile("smooth")).loadsWithSuccess().getLoadedModule();
    return Map.of("expected", module.toString());
  }

  static class ArgumentsProvider extends GoldenFilesArgumentsProvider {
    public ArgumentsProvider() {
      super(Path.of(TESTS_DIR), "smooth");
    }
  }
}
