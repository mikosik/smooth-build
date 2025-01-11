package org.smoothbuild.systemtest.cli;

import static java.lang.String.format;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.systemtest.SystemTestContext;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportError;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportInfo;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportWarning;

public class LoggingTest extends SystemTestContext {
  private static final String LOG_MESSAGE = "WARNING: my-message-to-log";

  @ParameterizedTest
  @MethodSource("test_cases")
  public void filter_logs_option_filters_logs_below_threshold(
      ContextInitializer userModuleCreator, String logLevel, boolean logShown) throws Throwable {
    userModuleCreator.initialize(this);
    runSmoothBuild("--filter-logs=" + logLevel, "result");
    if (logShown) {
      assertSystemOutContains(LOG_MESSAGE);
    } else {
      assertSystemOutDoesNotContain(LOG_MESSAGE);
    }
  }

  private static Stream<Arguments> test_cases() {
    return Stream.of(
        arguments((ContextInitializer) LoggingTest::createModuleWithError, "fatal", false),
        arguments((ContextInitializer) LoggingTest::createModuleWithError, "error", true),
        arguments((ContextInitializer) LoggingTest::createModuleWithError, "warning", true),
        arguments((ContextInitializer) LoggingTest::createModuleWithError, "info", true),
        arguments((ContextInitializer) LoggingTest::createModuleWithWarning, "fatal", false),
        arguments((ContextInitializer) LoggingTest::createModuleWithWarning, "error", false),
        arguments((ContextInitializer) LoggingTest::createModuleWithWarning, "warning", true),
        arguments((ContextInitializer) LoggingTest::createModuleWithWarning, "info", true),
        arguments((ContextInitializer) LoggingTest::createModuleWithInfo, "fatal", false),
        arguments((ContextInitializer) LoggingTest::createModuleWithInfo, "error", false),
        arguments((ContextInitializer) LoggingTest::createModuleWithInfo, "warning", false),
        arguments((ContextInitializer) LoggingTest::createModuleWithInfo, "info", true));
  }

  private static void createModuleWithError(SystemTestContext testCase) throws IOException {
    testCase.createNativeJar(ReportError.class);
    testCase.createUserModule(format(
        """
            @Native("%s")
            A reportError<A>(String message);
            Int result = reportError("%s");
            """,
        ReportError.class.getCanonicalName(), LOG_MESSAGE));
  }

  private static void createModuleWithWarning(SystemTestContext testCase) throws IOException {
    testCase.createNativeJar(ReportWarning.class);
    testCase.createUserModule(format(
        """
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("%s");
            """,
        ReportWarning.class.getCanonicalName(), LOG_MESSAGE));
  }

  private static void createModuleWithInfo(SystemTestContext testCase) throws IOException {
    testCase.createNativeJar(ReportInfo.class);
    testCase.createUserModule(format(
        """
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("%s");
            """,
        ReportInfo.class.getCanonicalName(), LOG_MESSAGE));
  }

  @FunctionalInterface
  public interface ContextInitializer {
    public void initialize(SystemTestContext testCase) throws Throwable;
  }
}
