package org.smoothbuild.app;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.command.CommandLineParserExecutor;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.parse.ModuleParserExecutor;
import org.smoothbuild.task.exec.SmoothExecutor;

import com.google.common.collect.ImmutableList;

public class SmoothApp {
  private final UserConsole userConsole;
  private final DetectingErrorsMessageListener messages;
  private final Cleaner cleaner;
  private final CommandLineParserExecutor commandLineParserExecutor;
  private final ModuleParserExecutor moduleParserExecutor;
  private final SmoothExecutor smoothExecutor;

  @Inject
  public SmoothApp(UserConsole userConsole, MessageListener messageListener, Cleaner cleaner,
      CommandLineParserExecutor commandLineParserExecutor,
      ModuleParserExecutor moduleParserExecutor, SmoothExecutor smoothExecutor) {
    this.userConsole = userConsole;
    this.messages = new DetectingErrorsMessageListener(messageListener);
    this.cleaner = cleaner;
    this.commandLineParserExecutor = commandLineParserExecutor;
    this.moduleParserExecutor = moduleParserExecutor;
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
    CommandLineArguments args = commandLineParserExecutor.execute(commandLine);
    if (args == null) {
      return;
    }

    Module module = moduleParserExecutor.execute(args);
    if (userConsole.isErrorReported()) {
      return;
    }

    smoothExecutor.execute(args, module, messages);
  }
}
