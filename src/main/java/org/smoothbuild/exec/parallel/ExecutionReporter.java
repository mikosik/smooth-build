package org.smoothbuild.exec.parallel;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.exec.base.MessageTuple.level;
import static org.smoothbuild.exec.base.MessageTuple.text;
import static org.smoothbuild.exec.compute.RealTask.NAME_LENGTH_LIMIT;
import static org.smoothbuild.exec.compute.ResultSource.EXECUTION;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.ResultSource;
import org.smoothbuild.exec.compute.Task;

/**
 * This class is thread-safe.
 */
public class ExecutionReporter {
  private final Reporter reporter;

  @Inject
  public ExecutionReporter(Reporter reporter) {
    this.reporter = reporter;
  }

  public void report(Task task, Computed computed) {
    ResultSource resultSource = computed.resultSource();
    if (computed.hasOutput()) {
      print(task, resultSource, computed.output().messages());
    } else {
      Log error = error(
          "Execution failed with:\n" + getStackTraceAsString(computed.exception()));
      print(task, list(error), resultSource);
    }
  }

  public void reportComputerException(Task task, Throwable throwable) {
    Log fatal = fatal(
        "Internal smooth error, computation failed with:" + getStackTraceAsString(throwable));
    ExecutionReporter.this.print(task, list(fatal), EXECUTION.toString());
  }

  private void print(Task task, ResultSource resultSource, Array messages) {
    var logs = map(messages.elements(Tuple.class), m -> new Log(level(m), text(m)));
    print(task, logs, resultSource);
  }

  public void print(Task task, List<Log> logs) {
    print(task, logs, "");
  }

  public void print(Task task, List<Log> logs, ResultSource resultSource) {
    print(task, logs, resultSource.toString());
  }

  private void print(Task task, List<Log> logs, String resultSource) {
    reporter.report(task, header(task, resultSource), logs);
  }

  private static String header(Task task, String resultSource) {
    String nameString = task.name();
    String locationString = task.location().toString();

    String nameColumn = padEnd(nameString, NAME_LENGTH_LIMIT + 1, ' ');
    String locationColumn = resultSource.isEmpty()
        ? locationString
        : padEnd(locationString, 30, ' ') + " ";
    return nameColumn + locationColumn + resultSource;
  }
}
