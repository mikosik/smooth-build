package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.work.build.CommandLineParserPhase;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.parse.ModuleParserPhase;
import org.smoothbuild.task.exec.ExecutionData;
import org.smoothbuild.task.exec.SmoothExecutorPhase;

import com.google.common.collect.ImmutableList;

public class Build implements Command {
  @Inject
  private UserConsole userConsole;
  @Inject
  private CommandLineParserPhase commandLineParserPhase;
  @Inject
  private ModuleParserPhase moduleParserPhase;
  @Inject
  private SmoothExecutorPhase smoothExecutorPhase;

  @Override
  public int run(String... functions) {
    List<String> functionList = ImmutableList.copyOf(functions).subList(1, functions.length);
    Set<Name> args = commandLineParserPhase.execute(functionList);

    if (!userConsole.isProblemReported()) {
      Module module = moduleParserPhase.execute(args);
      if (!userConsole.isProblemReported()) {
        smoothExecutorPhase.execute(new ExecutionData(args, module));
      }
    }

    userConsole.printFinalSummary();
    return userConsole.isProblemReported() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
