package org.smoothbuild.exec.task;

import static org.smoothbuild.lang.object.base.Messages.isEmpty;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.object.base.Array;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

public class TaskReporter {
  private final Console console;

  @Inject
  public TaskReporter(Console console) {
    this.console = console;
  }

  public void report(Task task, TaskResult result) {
    if (result.hasOutput()) {
      Array messages = result.output().messages();
      if (!isEmpty(messages)) {
        console.print(header(task, result), messages);
      }
    } else {
      console.print(header(task, result), result.failure());
    }
  }

  @VisibleForTesting
  static String header(Task task, TaskResult result) {
    String locationString = task.location().toString();
    int paddedLength = Console.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    String cached = result.isFromCache() ? " CACHED" : "";
    return name + locationString + cached;
  }
}
