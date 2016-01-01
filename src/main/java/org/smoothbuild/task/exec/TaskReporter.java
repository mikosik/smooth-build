package org.smoothbuild.task.exec;

import static com.google.common.base.Throwables.getStackTraceAsString;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.task.base.Task;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class TaskReporter {
  private final Console console;

  @Inject
  public TaskReporter(Console console) {
    this.console = console;
  }

  public void report(Task task, boolean resultFromCache) {
    ImmutableList<Message> messages = task.output().messages();
    if (!(task.isInternal() && messages.isEmpty())) {
      String header = header(task, resultFromCache);
      console.print(header, messages);
    }
  }

  @VisibleForTesting
  static String header(Task task, boolean isResultFromCached) {
    String locationString = task.codeLocation().toString();
    int paddedLength = Console.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    String cached = isResultFromCached ? " CACHED" : "";
    return name + locationString + cached;
  }

  public void reportCrash(RuntimeException e) {
    console.error(getStackTraceAsString(e));
  }
}
