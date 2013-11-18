package org.smoothbuild.app;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.command.CommandLineParserPhase;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.parse.ModuleParserPhase;
import org.smoothbuild.task.exec.ExecutionData;
import org.smoothbuild.task.exec.SmoothExecutorPhase;

import com.google.common.collect.ImmutableList;

public class BuildWorker {
  private final UserConsole userConsole;
  private final CommandLineParserPhase commandLineParserPhase;
  private final ModuleParserPhase moduleParserPhase;
  private final SmoothExecutorPhase smoothExecutorPhase;

  @Inject
  public BuildWorker(UserConsole userConsole, CommandLineParserPhase commandLineParserPhase,
      ModuleParserPhase moduleParserPhase, SmoothExecutorPhase smoothExecutorPhase) {
    this.userConsole = userConsole;
    this.commandLineParserPhase = commandLineParserPhase;
    this.moduleParserPhase = moduleParserPhase;
    this.smoothExecutorPhase = smoothExecutorPhase;
  }

  public boolean run(List<String> functions) {
    CommandLineArguments args = commandLineParserPhase.execute(ImmutableList.copyOf(functions));

    if (args != null) {
      Module module = moduleParserPhase.execute(args);
      if (!userConsole.isProblemReported()) {
        smoothExecutorPhase.execute(new ExecutionData(args, module));
      }
    }

    userConsole.printFinalSummary();
    return !userConsole.isProblemReported();
  }
}
