package org.smoothbuild.cli;

import static org.smoothbuild.cli.CommandHelper.runCommand;

import java.util.List;
import java.util.concurrent.Callable;

import org.smoothbuild.exec.run.TreeRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = TreeCommand.NAME,
    description = "Print execution tree for specified function(s)"
)
public class TreeCommand extends StandardOptions implements Callable<Integer> {
  public static final String NAME = "tree";

  @Parameters(
      paramLabel = "<function>",
      arity = "1..*",
      description = "function(s) which execution tree is printed")
  List<String> functions;

  @Override
  public Integer call() {
    return runCommand(injector -> injector.getInstance(TreeRunner.class).run(functions));
  }
}
