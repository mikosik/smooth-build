package org.smoothbuild.exec.parallel;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.exec.base.MessageTuple.level;
import static org.smoothbuild.exec.base.MessageTuple.text;
import static org.smoothbuild.exec.compute.ResultSource.EXECUTION;
import static org.smoothbuild.exec.compute.Task.NAME_LENGTH_LIMIT;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.ResultSource;
import org.smoothbuild.exec.compute.Task;

import com.google.common.collect.Streams;

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
      print(task, resultSource, list(error));
    }
  }

  public void reportComputerException(Task task, Throwable throwable) {
    Log fatal = fatal(
        "Internal smooth error, computation failed with:" + getStackTraceAsString(throwable));
    reporter.report(task, header(task, EXECUTION), list(fatal));
  }

  private void print(Task task, ResultSource resultSource, Array messages) {
    List<Log> logs = Streams.stream(messages.asIterable(Tuple.class))
        .map(m -> new Log(level(m), text(m)))
        .collect(toList());
    print(task, resultSource, logs);
  }

  public void print(Task task, ResultSource resultSource, List<Log> logs) {
    reporter.report(task, header(task, resultSource), logs);
  }

  private static String header(Task task, ResultSource resultSource) {
    String nameString = task.name();
    String locationString = task.location().toString();
    String sourceString = resultSource.toString();

    String nameColumn = padEnd(nameString, NAME_LENGTH_LIMIT + 1, ' ');
    String locationColumn = sourceString.isEmpty()
        ? locationString
        : padEnd(locationString, 30, ' ') + " ";
    return nameColumn + locationColumn + sourceString;
  }
}
