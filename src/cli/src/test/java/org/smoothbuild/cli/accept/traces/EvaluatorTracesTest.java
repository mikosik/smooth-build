package org.smoothbuild.cli.accept.traces;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.schedule.SingleThreadRunnableScheduler;
import org.smoothbuild.common.testing.GoldenFilesArgumentsProvider;
import org.smoothbuild.common.testing.GoldenFilesTestCase;
import org.smoothbuild.evaluator.dagger.EvaluatorTestContext;

public class EvaluatorTracesTest extends EvaluatorTestContext {
  private static final String TESTS_DIR = "src/test/java/org/smoothbuild/cli/accept/traces";

  public EvaluatorTracesTest() {
    super(new SingleThreadRunnableScheduler());
  }

  @ParameterizedTest
  @ArgumentsSource(ArgumentsProvider.class)
  void test_compile(GoldenFilesTestCase testCase) throws IOException {
    testCase.assertWithGoldenFiles(generateFiles(testCase));
  }

  private Map<String, String> generateFiles(GoldenFilesTestCase testCase) throws IOException {
    var code = testCase.readFile("smooth");
    var actualLogs = compileAndGetLogs(code);
    return Map.of("logs", actualLogs);
  }

  private String compileAndGetLogs(String code) throws IOException {
    createUserModule(code);
    evaluate("result");
    return reporter().reports().map(Report::toPrettyString).toString("\n");
  }

  static class ArgumentsProvider extends GoldenFilesArgumentsProvider {
    public ArgumentsProvider() {
      super(Path.of(TESTS_DIR), "smooth");
    }
  }
}
