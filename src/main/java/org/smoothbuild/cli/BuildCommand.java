package org.smoothbuild.cli;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.MainModule;
import org.smoothbuild.cli.work.build.CommandLineArguments;
import org.smoothbuild.cli.work.build.CommandLineParserPhase;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.parse.ModuleParserPhase;
import org.smoothbuild.task.exec.ExecutionData;
import org.smoothbuild.task.exec.SmoothExecutorPhase;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;

public class BuildCommand implements Command {
  @Override
  public String shortDescription() {
    return "Build artifact(s) by running specified function(s)";
  }

  @Override
  public String longDescription() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth build <function>...\n");
    builder.append("\n");
    builder.append(shortDescription() + "\n");
    builder.append("\n");
    builder.append("  <function>  function which execution result is saved as artifact");
    return builder.toString();
  }

  @Override
  public int execute(String[] args) {
    return Guice.createInjector(new MainModule()).getInstance(Build.class).run(args);
  }

  public static class Build {
    @Inject
    private UserConsole userConsole;
    @Inject
    private CommandLineParserPhase commandLineParserPhase;
    @Inject
    private ModuleParserPhase moduleParserPhase;
    @Inject
    private SmoothExecutorPhase smoothExecutorPhase;

    public int run(String... functions) {
      List<String> functionList = ImmutableList.copyOf(functions).subList(1, functions.length);
      CommandLineArguments args = commandLineParserPhase.execute(functionList);

      if (!userConsole.isProblemReported()) {
        Module module = moduleParserPhase.execute(args);
        if (!userConsole.isProblemReported()) {
          smoothExecutorPhase.execute(new ExecutionData(args, module));
        }
      }

      userConsole.printFinalSummary();
      return userConsole.isProblemReported() ? 1 : 0;
    }
  }
}
