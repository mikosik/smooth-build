package org.smoothbuild.cli;

import static org.smoothbuild.cli.CommandHelper.runCommand;

import java.util.List;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = BuildCommand.NAME,
    description = "Build artifact(s) by running specified function(s)"
)
public class BuildCommand extends StandardOptions implements Callable<Integer> {
  public static final String NAME = "build";

  @Parameters(
      paramLabel = "<function>",
      arity = "1..*",
      description = "function(s) which results are saved as artifacts")
  List<String> functions;

  @Override
  public Integer call() {
    return runCommand(injector -> injector.getInstance(Build.class).run(functions));
  }
}
