package org.smoothbuild.vm.execute;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Log.info;
import static org.smoothbuild.out.log.Log.warning;
import static org.smoothbuild.testing.TestContext.loc;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.execute.TaskKind.CALL;
import static org.smoothbuild.vm.report.TaskMatchers.ALL;
import static org.smoothbuild.vm.report.TaskMatchers.NONE;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Console;
import org.smoothbuild.out.report.ConsoleReporter;

public class TaskReporterTest {
  private static final Log FATAL_LOG = fatal("fatal message");
  private static final Log ERROR_LOG = error("error message");
  private static final Log WARNING_LOG = warning("warning message");
  private static final Log INFO_LOG = info("info message");

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final Console console = new Console(new PrintWriter(outputStream, true));

  @ParameterizedTest
  @MethodSource("filtered_logs_cases")
  public void when_filter_matches_then_logs_which_passes_threshold_are_logged(
      Level level, List<Log> loggedLogs) {
    var reporter = new ConsoleReporter(console, level);
    var taskReporter = new TaskReporter(ALL, reporter);
    taskReporter.report(taskInfo(), "header", logsWithAllLevels());
    assertThat(outputStream.toString())
        .contains(ConsoleReporter.toText("header", loggedLogs));
  }

  public static List<Arguments> filtered_logs_cases() {
    return List.of(
        arguments(FATAL, list(FATAL_LOG)),
        arguments(ERROR, list(FATAL_LOG, ERROR_LOG)),
        arguments(WARNING, list(FATAL_LOG, ERROR_LOG, WARNING_LOG)),
        arguments(INFO, list(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG))
    );
  }

  @ParameterizedTest
  @MethodSource("all_levels")
  public void when_filter_doesnt_match_then_no_log_is_logged(Level level) {
    var reporter = new ConsoleReporter(console, level);
    var taskReporter = new TaskReporter(NONE, reporter);
    taskReporter.report(taskInfo(), "header", logsWithAllLevels());
    assertThat(outputStream.toString())
        .isEmpty();
  }

  private static List<Level> all_levels() {
    return List.of(Level.values());
  }

  private static List<Log> logsWithAllLevels() {
    return list(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG);
  }

  private static TaskInfo taskInfo() {
    return new TaskInfo(CALL, "name", loc());
  }
}
