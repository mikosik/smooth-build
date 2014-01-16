package org.smoothbuild.message.listen;

public abstract class MessageCatchingExecutor<A, R> {
  private final UserConsole userConsole;
  private final LoggedMessages loggedMessages;
  private final String name;

  public MessageCatchingExecutor(UserConsole userConsole, String name, LoggedMessages loggedMessages) {
    this.userConsole = userConsole;
    this.name = name;
    this.loggedMessages = loggedMessages;
  }

  public R execute(A argument) {
    try {
      return executeImpl(argument);
    } catch (ErrorMessageException e) {
      loggedMessages.log(e.errorMessage());
    } catch (PhaseFailedException e) {
      if (!loggedMessages.containsProblems()) {
        loggedMessages.log(new PhaseFailedWithoutErrorError());
      }
    } finally {
      if (loggedMessages.containsProblems()) {
        userConsole.report(name, loggedMessages);
      }
    }
    return null;
  }

  public abstract R executeImpl(A arguments);
}
