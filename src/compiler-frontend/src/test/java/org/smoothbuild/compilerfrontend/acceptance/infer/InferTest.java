package org.smoothbuild.compilerfrontend.acceptance.infer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.testing.GoldenFilesArgumentsProvider;
import org.smoothbuild.common.testing.GoldenFilesTestCase;
import org.smoothbuild.compilerfrontend.lang.define.SModule;
import org.smoothbuild.compilerfrontend.testing.FrontendCompileTester;

/**
 * Test verifying that type that is not declared explicitly is inferred correctly or error is
 * reported when it is impossible to infer it.
 */
public class InferTest extends FrontendCompileTester {
  private static final String TESTS_DIR =
      "src/test/java/org/smoothbuild/compilerfrontend/acceptance/infer";

  @ParameterizedTest
  @ArgumentsSource(ArgumentsProvider.class)
  void test_inference(GoldenFilesTestCase testCase) throws IOException {
    testCase.assertWithGoldenFiles(assertTest(testCase));
  }

  private Map<String, String> assertTest(GoldenFilesTestCase testCase) throws IOException {
    var module = module(testCase.readFile("smooth")).loadModule();
    var moduleText = module.toMaybe().map(SModule::toSourceCode).getOr("");
    var logsText = module.logs().toString("\n");
    return Map.of("expected", moduleText, "logs", logsText);
  }

  static class ArgumentsProvider extends GoldenFilesArgumentsProvider {
    public ArgumentsProvider() {
      super(Path.of(TESTS_DIR), "smooth");
    }
  }
}
