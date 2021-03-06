package org.smoothbuild.acceptance.cli;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.util.Strings.unlines;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.ReportError;
import org.smoothbuild.acceptance.testing.ReportInfo;
import org.smoothbuild.acceptance.testing.ReportWarning;

public class LoggingTest extends AcceptanceTestCase {
  private static final String LOG_MESSAGE = "WARNING: my-warning-message";

  @ParameterizedTest
  @MethodSource("test_cases")
  public void name(
      TestCaseInitializer userModuleCreator, String logLevel, boolean logShown) throws Throwable {
    userModuleCreator.initialize(this);
    runSmoothBuild("--log-level=" + logLevel, "result");
    if (logShown) {
      assertSysOutContains(LOG_MESSAGE);
    } else {
      assertSysOutDoesNotContain(LOG_MESSAGE);
    }
  }

  private static Stream<Arguments> test_cases() {
    return Stream.of(
        arguments((TestCaseInitializer) LoggingTest::createModuleWithError, "fatal", false),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithError, "error", true),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithError, "warning", true),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithError, "info", true),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithWarning, "fatal", false),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithWarning, "error", false),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithWarning, "warning", true),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithWarning, "info", true),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithInfo, "fatal", false),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithInfo, "error", false),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithInfo, "warning", false),
        arguments((TestCaseInitializer) LoggingTest::createModuleWithInfo, "info", true)
    );
  }

  private static void createModuleWithError(AcceptanceTestCase testCase) throws IOException {
    testCase.createNativeJar(ReportError.class);
    testCase.createUserModule(unlines(
        "Nothing reportError(String message);",
        "result = reportError('" + LOG_MESSAGE + "');",
        ""
    ));
  }

  private static void createModuleWithWarning(AcceptanceTestCase testCase) throws IOException {
    testCase.createNativeJar(ReportWarning.class);
    testCase.createUserModule(unlines(
        "String reportWarning(String message);",
        "result = reportWarning('" + LOG_MESSAGE + "');",
        ""
    ));
  }

  private static void createModuleWithInfo(AcceptanceTestCase testCase) throws IOException {
    testCase.createNativeJar(ReportInfo.class);
    testCase.createUserModule(unlines(
        "String reportInfo(String message);",
        "result = reportInfo('" + LOG_MESSAGE + "');",
        ""
    ));
  }

  @FunctionalInterface
  public interface TestCaseInitializer {
    public void initialize(AcceptanceTestCase testCase) throws Throwable;
  }
}
