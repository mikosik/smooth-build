package org.smoothbuild.app;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.command.CommandLineParser;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.parse.ModuleParser;
import org.smoothbuild.task.exec.SmoothExecutor;

public class SmoothApp {
  private final UserConsole userConsole;
  private final DetectingErrorsMessageListener messages;
  private final Cleaner cleaner;
  private final CommandLineParser commandLineParser;
  private final ModuleParser moduleParser;
  private final SmoothExecutor smoothExecutor;

  @Inject
  public SmoothApp(UserConsole userConsole, MessageListener messageListener, Cleaner cleaner,
      CommandLineParser commandLineParser, ModuleParser moduleParser, SmoothExecutor smoothExecutor) {
    this.userConsole = userConsole;
    this.messages = new DetectingErrorsMessageListener(messageListener);
    this.cleaner = cleaner;
    this.commandLineParser = commandLineParser;
    this.moduleParser = moduleParser;
    this.smoothExecutor = smoothExecutor;
  }

  public void run(String... commandLine) {
    cleaner.clearBuildDir();

    try {
      CommandLineArguments args = commandLineParser.parse(commandLine);

      Module module = moduleParser.createModule(messages, args);
      if (messages.errorDetected()) {
        return;
      }

      smoothExecutor.execute(args, module, messages);
    } catch (ErrorMessageException e) {
      messages.report(e.errorMessage());
    }

    userConsole.printFinalSummary();
  }
}
