package org.smoothbuild.exec.parallel;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.exec.base.MessageStruct.level;
import static org.smoothbuild.exec.base.MessageStruct.text;
import static org.smoothbuild.exec.compute.ResSource.EXECUTION;
import static org.smoothbuild.exec.job.TaskInfo.NAME_LENGTH_LIMIT;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.db.object.obj.val.TupleB;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.ResSource;
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
    ResSource resSource = computed.resSource();
    if (computed.hasOutput()) {
      print(taskInfo, resSource, computed.output().messages());
    } else {
      Log error = error(
          "Execution failed with:\n" + getStackTraceAsString(computed.exception()));
      print(taskInfo, list(error), resSource);
    }
  }

  public void reportComputerException(TaskInfo taskInfo, Throwable throwable) {
    Log fatal = fatal(
        "Internal smooth error, computation failed with:" + getStackTraceAsString(throwable));
    ExecutionReporter.this.print(taskInfo, list(fatal), EXECUTION.toString());
  }

  private void print(TaskInfo taskInfo, ResSource resSource, ArrayB messages) {
    var logs = map(messages.elems(TupleB.class), m -> new Log(level(m), text(m)));
    print(taskInfo, logs, resSource);
  }

  public void print(TaskInfo taskInfo, List<Log> logs) {
    print(taskInfo, logs, "");
  }

  public void print(TaskInfo taskInfo, List<Log> logs, ResSource resSource) {
    print(taskInfo, logs, resSource.toString());
  }

  private void print(TaskInfo taskInfo, List<Log> logs, String resultSource) {
    reporter.report(taskInfo, header(taskInfo, resultSource), logs);
  }

  // Visible for testing
  static String header(TaskInfo taskInfo, String resultSource) {
    String nameString = taskInfo.name();
    String locString = taskInfo.loc().toString();

    String nameColumn = padEnd(nameString, NAME_LENGTH_LIMIT + 1, ' ');
    String locColumn = resultSource.isEmpty()
        ? locString
        : padEnd(locString, 30, ' ') + " ";
    return nameColumn + locColumn + resultSource;
  }
}
