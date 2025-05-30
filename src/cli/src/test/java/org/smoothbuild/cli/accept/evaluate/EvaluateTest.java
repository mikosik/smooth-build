package org.smoothbuild.cli.accept.evaluate;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.testing.GoldenFilesArgumentsProvider;
import org.smoothbuild.common.testing.GoldenFilesTestCase;
import org.smoothbuild.evaluator.dagger.EvaluatorTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.StringIdentity;
import org.smoothbuild.virtualmachine.testing.func.nativ.ThrowException;

public class EvaluateTest extends EvaluatorTestContext {
  private static final String TESTS_DIR = "src/test/java/org/smoothbuild/cli/accept/evaluate";

  @ParameterizedTest
  @ArgumentsSource(ArgumentsProvider.class)
  void test_compile(GoldenFilesTestCase testCase) throws IOException {
    testCase.assertWithGoldenFiles(generateFiles(testCase));
  }

  private Map<String, String> generateFiles(GoldenFilesTestCase testCase) throws IOException {
    var code = testCase.readFile("smooth");
    var importedCode = testCase.readFileMaybe("imported");
    var artifact = evaluate(code, importedCode);
    return Map.of("artifact", artifact);
  }

  private String evaluate(String code, Maybe<String> importedModule) throws IOException {
    createUserModule(code, StringIdentity.class, ThrowException.class);
    evaluate("result");
    return artifact().toString();
  }

  static class ArgumentsProvider extends GoldenFilesArgumentsProvider {
    public ArgumentsProvider() {
      super(Path.of(TESTS_DIR), "smooth");
    }
  }
}
