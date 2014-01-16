package org.smoothbuild.message.listen;

public abstract class MessageCatchingExecutor<A, R> {
  private final UserConsole userConsole;
  private final MessageGroup messageGroup;
  private final String name;

  public MessageCatchingExecutor(UserConsole userConsole, String name, MessageGroup messageGroup) {
    this.userConsole = userConsole;
    this.name = name;
    this.messageGroup = messageGroup;
  }

  public R execute(A argument) {
    try {
      return executeImpl(argument);
    } catch (ErrorMessageException e) {
      messageGroup.report(e.errorMessage());
    } catch (PhaseFailedException e) {
      if (!messageGroup.containsProblems()) {
        messageGroup.report(new PhaseFailedWithoutErrorError());
      }
    } finally {
      if (messageGroup.containsProblems()) {
        userConsole.report(name, messageGroup);
      }
    }
    return null;
  }

  public abstract R executeImpl(A arguments);
}
