package org.smoothbuild.vm.parallel;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.eval.artifact.MessageStruct.level;
import static org.smoothbuild.eval.artifact.MessageStruct.text;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.compute.ResSource.EXECUTION;
import static org.smoothbuild.vm.job.job.JobInfo.NAME_LENGTH_LIMIT;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.vm.compute.Computed;
import org.smoothbuild.vm.compute.ResSource;
import org.smoothbuild.vm.job.job.JobInfo;

/**
 * This class is thread-safe.
 */
public class ExecutionReporter {
  private final Reporter reporter;

  @Inject
  public ExecutionReporter(Reporter reporter) {
    this.reporter = reporter;
  }

  public void report(JobInfo jobInfo, Computed computed) {
    ResSource resSource = computed.resSource();
    if (computed.hasOutput()) {
      print(jobInfo, resSource, computed.output().messages());
    } else {
      Log error = error(
          "Execution failed with:\n" + getStackTraceAsString(computed.exception()));
      print(jobInfo, list(error), resSource);
    }
  }

  public void reportComputerException(JobInfo jobInfo, Throwable throwable) {
    Log fatal = fatal(
        "Internal smooth error, computation failed with:" + getStackTraceAsString(throwable));
    ExecutionReporter.this.print(jobInfo, list(fatal), EXECUTION.toString());
  }

  private void print(JobInfo jobInfo, ResSource resSource, ArrayB messages) {
    var logs = map(messages.elems(TupleB.class), m -> new Log(level(m), text(m)));
    print(jobInfo, logs, resSource);
  }

  public void print(JobInfo jobInfo, List<Log> logs) {
    print(jobInfo, logs, "");
  }

  public void print(JobInfo jobInfo, List<Log> logs, ResSource resSource) {
    print(jobInfo, logs, resSource.toString());
  }

  private void print(JobInfo jobInfo, List<Log> logs, String resultSource) {
    reporter.report(jobInfo, header(jobInfo, resultSource), logs);
  }

  // Visible for testing
  static String header(JobInfo jobInfo, String resultSource) {
    String nameString = jobInfo.name();
    String locString = jobInfo.loc().toString();

    String nameColumn = padEnd(nameString, NAME_LENGTH_LIMIT + 1, ' ');
    String locColumn = resultSource.isEmpty()
        ? locString
        : padEnd(locString, 30, ' ') + " ";
    return nameColumn + locColumn + resultSource;
  }
}
