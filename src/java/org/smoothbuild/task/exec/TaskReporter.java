package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.UserConsole;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class TaskReporter {
  private final UserConsole userConsole;

  @Inject
  public TaskReporter(UserConsole userConsole) {
    this.userConsole = userConsole;
  }

  public void report(Task<?> task, boolean resultFromCache) {
    ImmutableList<Message> messages = task.output().messages();
    if (!(task.isInternal() && messages.isEmpty())) {
      String header = header(task, resultFromCache);
      userConsole.print(header, messages);
    }
  }

  @VisibleForTesting
  static String header(Task<?> task, boolean isResultFromCached) {
    String locationString = task.codeLocation().toString();
    int paddedLength = UserConsole.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    String cached = isResultFromCached ? " CACHED" : "";
    return name + locationString + cached;
  }
}
