package org.smoothbuild.app;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.command.CommandLineParserPhase;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.parse.ModuleParserPhase;
import org.smoothbuild.task.exec.ExecutionData;
import org.smoothbuild.task.exec.SmoothExecutorPhase;

import com.google.common.collect.ImmutableList;

public class SmoothApp {
  private final UserConsole userConsole;
  private final Cleaner cleaner;
  private final CommandLineParserPhase commandLineParserPhase;
  private final ModuleParserPhase moduleParserPhase;
  private final SmoothExecutorPhase smoothExecutorPhase;

  @Inject
  public SmoothApp(UserConsole userConsole, Cleaner cleaner,
      CommandLineParserPhase commandLineParserPhase, ModuleParserPhase moduleParserPhase,
      SmoothExecutorPhase smoothExecutorPhase) {
    this.userConsole = userConsole;
    this.cleaner = cleaner;
    this.commandLineParserPhase = commandLineParserPhase;
    this.moduleParserPhase = moduleParserPhase;
    this.smoothExecutorPhase = smoothExecutorPhase;
  }

  public void run(String... commandLine) {
    cleaner.clearBuildDir();

    CommandLineArguments args = commandLineParserPhase.execute(ImmutableList.copyOf(commandLine));

    if (args != null) {
      Module module = moduleParserPhase.execute(args);
      if (!userConsole.isErrorReported()) {
        smoothExecutorPhase.execute(new ExecutionData(args, module));
      }
    }

    userConsole.printFinalSummary();
  }
}
