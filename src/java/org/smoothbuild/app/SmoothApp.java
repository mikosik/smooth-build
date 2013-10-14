package org.smoothbuild.app;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.command.CommandLineParserPhase;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.parse.ModuleParserPhase;
import org.smoothbuild.task.exec.SmoothExecutor;

import com.google.common.collect.ImmutableList;

public class SmoothApp {
  private final UserConsole userConsole;
  private final DetectingErrorsMessageListener messages;
  private final Cleaner cleaner;
  private final CommandLineParserPhase commandLineParserPhase;
  private final ModuleParserPhase moduleParserPhase;
  private final SmoothExecutor smoothExecutor;

  @Inject
  public SmoothApp(UserConsole userConsole, MessageListener messageListener, Cleaner cleaner,
      CommandLineParserPhase commandLineParserPhase,
      ModuleParserPhase moduleParserPhase, SmoothExecutor smoothExecutor) {
    this.userConsole = userConsole;
    this.messages = new DetectingErrorsMessageListener(messageListener);
    this.cleaner = cleaner;
    this.commandLineParserPhase = commandLineParserPhase;
    this.moduleParserPhase = moduleParserPhase;
    this.smoothExecutor = smoothExecutor;
  }

  public void run(String... commandLine) {
    cleaner.clearBuildDir();

    try {
      runImpl(ImmutableList.copyOf(commandLine));
    } catch (ErrorMessageException e) {
      messages.report(e.errorMessage());
    }

    userConsole.printFinalSummary();
  }

  private void runImpl(List<String> commandLine) {
    CommandLineArguments args = commandLineParserPhase.execute(commandLine);
    if (args == null) {
      return;
    }

    Module module = moduleParserPhase.execute(args);
    if (userConsole.isErrorReported()) {
      return;
    }

    smoothExecutor.execute(args, module, messages);
  }
}
