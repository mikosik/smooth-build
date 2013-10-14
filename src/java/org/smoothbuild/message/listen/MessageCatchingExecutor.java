package org.smoothbuild.message.listen;


public abstract class MessageCatchingExecutor<A, R> {
  private final UserConsole userConsole;
  private MessageGroup messageGroup;

  public MessageCatchingExecutor(UserConsole userConsole, String name) {
    this(userConsole, new MessageGroup(name));
  }

  public MessageCatchingExecutor(UserConsole userConsole, MessageGroup messageGroup) {
    this.userConsole = userConsole;
    this.messageGroup = messageGroup;
  }

  public R execute(A argument) {
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
