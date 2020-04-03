package org.smoothbuild.exec.task.parallel;

import static org.smoothbuild.lang.object.base.Messages.isEmpty;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.task.base.MaybeComputed;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.object.base.Array;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

/**
 * This class is thread-safe.
 */
public class ExecutionReporter {
  private final Console console;

  @Inject
  public ExecutionReporter(Console console) {
    this.console = console;
  }

  public void report(Task task, MaybeComputed maybeComputed, boolean fromCache) {
    if (maybeComputed.hasComputed()) {
      MaybeOutput maybeOutput = maybeComputed.computed();
      if (maybeOutput.hasOutput()) {
        Array messages = maybeOutput.output().messages();
        if (!isEmpty(messages)) {
          console.print(header(task, fromCache), messages);
        }
      } else {
        console.print(header(task, fromCache), maybeOutput.exception());
      }
    } else {
      report(maybeComputed.throwable());
    }
  }

  public void report(Throwable throwable) {
    console.print("Execution failed with:\n", throwable);
  }

  @VisibleForTesting
  static String header(Task task, boolean fromCache) {
    String locationString = task.location().toString();
    int paddedLength = Console.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    return name + locationString + (fromCache ? " CACHED" : "");
  }
}
