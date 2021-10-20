package org.smoothbuild.exec.parallel;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.exec.base.MessageStruct.level;
import static org.smoothbuild.exec.base.MessageStruct.text;
import static org.smoothbuild.exec.compute.ResultSource.EXECUTION;
import static org.smoothbuild.exec.job.TaskInfo.NAME_LENGTH_LIMIT;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.ResultSource;
import org.smoothbuild.exec.job.TaskInfo;

/**
 * This class is thread-safe.
 */
public class ExecutionReporter {
  private final Reporter reporter;

  @Inject
  public ExecutionReporter(Reporter reporter) {
    this.reporter = reporter;
  }

  public void report(TaskInfo taskInfo, Computed computed) {
    ResultSource resultSource = computed.resultSource();
    if (computed.hasOutput()) {
      print(taskInfo, resultSource, computed.output().messages());
    } else {
      Log error = error(
          "Execution failed with:\n" + getStackTraceAsString(computed.exception()));
      print(taskInfo, list(error), resultSource);
    }
  }

  public void reportComputerException(TaskInfo taskInfo, Throwable throwable) {
    Log fatal = fatal(
        "Internal smooth error, computation failed with:" + getStackTraceAsString(throwable));
    ExecutionReporter.this.print(taskInfo, list(fatal), EXECUTION.toString());
  }

  private void print(TaskInfo taskInfo, ResultSource resultSource, Array messages) {
    var logs = map(messages.elements(Struc_.class), m -> new Log(level(m), text(m)));
    print(taskInfo, logs, resultSource);
  }

  public void print(TaskInfo taskInfo, List<Log> logs) {
    print(taskInfo, logs, "");
  }

  public void print(TaskInfo taskInfo, List<Log> logs, ResultSource resultSource) {
    print(taskInfo, logs, resultSource.toString());
  }

  private void print(TaskInfo taskInfo, List<Log> logs, String resultSource) {
    reporter.report(taskInfo, header(taskInfo, resultSource), logs);
  }

  // Visible for testing
  static String header(TaskInfo taskInfo, String resultSource) {
    String nameString = taskInfo.name();
    String locationString = taskInfo.location().toString();

    String nameColumn = padEnd(nameString, NAME_LENGTH_LIMIT + 1, ' ');
    String locationColumn = resultSource.isEmpty()
        ? locationString
        : padEnd(locationString, 30, ' ') + " ";
    return nameColumn + locationColumn + resultSource;
  }
}
