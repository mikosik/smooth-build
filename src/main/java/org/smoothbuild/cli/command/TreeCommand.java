package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;

import java.util.List;

import org.smoothbuild.cli.base.ExclusiveCommand;
import org.smoothbuild.exec.run.TreeRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = TreeCommand.NAME,
    description = "Print execution tree for specified function(s)"
)
public class TreeCommand extends ExclusiveCommand {
  public static final String NAME = "tree";

  @Parameters(
      paramLabel = "<function>",
      arity = "1..*",
      description = "function(s) which execution tree is printed")
  List<String> functions;

  @Override
  protected Integer invokeCall() {
    return createInjector(installationDir(), out(), logLevel)
        .getInstance(TreeRunner.class)
        .run(functions);
  }
}
