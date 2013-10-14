package org.smoothbuild.message.listen;

import org.smoothbuild.message.message.ErrorMessageException;

public abstract class MessageCatchingExecutor<A, R> {
  private final UserConsole userConsole;
  private final String name;

  public MessageCatchingExecutor(UserConsole userConsole, String name) {
    this.userConsole = userConsole;
    this.name = name;
  }

  public R execute(A argument) {
    MessageGroup messageGroup = new MessageGroup(name);
    try {
      return executeImpl(argument);
    } catch (ErrorMessageException e) {
      messageGroup.report(e.errorMessage());
    } finally {
      userConsole.report(messageGroup);
    }
    return null;
  }

  public abstract R executeImpl(A arguments);
}
