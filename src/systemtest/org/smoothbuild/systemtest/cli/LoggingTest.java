package org.smoothbuild.systemtest.cli;

import static java.lang.String.format;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.testing.nativefunc.ReportError;
import org.smoothbuild.testing.nativefunc.ReportInfo;
import org.smoothbuild.testing.nativefunc.ReportWarning;

public class LoggingTest extends SystemTestCase {
  private static final String LOG_MESSAGE = "WARNING: my-message-to-log";

  @ParameterizedTest
  @MethodSource("test_cases")
  public void log_level_option_filters_logs_below_threshold(
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

  private static void createModuleWithError(SystemTestCase testCase) throws IOException {
    testCase.createNativeJar(ReportError.class);
    testCase.createUserModule(format("""
            @Native("%s")
            Nothing reportError(String message);
            result = reportError("%s");
            """, ReportError.class.getCanonicalName(), LOG_MESSAGE));
  }

  private static void createModuleWithWarning(SystemTestCase testCase) throws IOException {
    testCase.createNativeJar(ReportWarning.class);
    testCase.createUserModule(format("""
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("%s");
            """, ReportWarning.class.getCanonicalName(), LOG_MESSAGE));
  }

  private static void createModuleWithInfo(SystemTestCase testCase) throws IOException {
    testCase.createNativeJar(ReportInfo.class);
    testCase.createUserModule(format("""
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("%s");
            """, ReportInfo.class.getCanonicalName(), LOG_MESSAGE));
  }

  @FunctionalInterface
  public interface TestCaseInitializer {
    public void initialize(SystemTestCase testCase) throws Throwable;
  }
}
