package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CommandHelper.runCommandExclusively;

import java.util.List;
import java.util.concurrent.Callable;

import org.smoothbuild.cli.base.LoggingCommand;
import org.smoothbuild.exec.run.TreeRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = TreeCommand.NAME,
    description = "Print execution tree for specified function(s)"
)
public class TreeCommand extends LoggingCommand implements Callable<Integer> {
  public static final String NAME = "tree";

  @Parameters(
      paramLabel = "<function>",
      arity = "1..*",
      description = "function(s) which execution tree is printed")
  List<String> functions;

  @Override
  public Integer call() {
    return runCommandExclusively(injector -> injector.getInstance(TreeRunner.class).run(functions));
  }
}
