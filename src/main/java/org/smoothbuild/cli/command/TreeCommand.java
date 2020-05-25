package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;
import static org.smoothbuild.cli.base.RunExclusively.runExclusively;

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
    return runExclusively(out(), this::treeCommand);
  }

  private int treeCommand() {
    return createInjector(out(), logLevel)
        .getInstance(TreeRunner.class)
        .run(functions);
  }
}
