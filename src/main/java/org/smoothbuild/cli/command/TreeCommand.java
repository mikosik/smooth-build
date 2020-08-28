package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.cli.base.ExclusiveCommand;
import org.smoothbuild.run.TreeRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = TreeCommand.NAME,
    description = "Print execution tree for specified value(s)"
)
public class TreeCommand extends ExclusiveCommand {
  public static final String NAME = "tree";

  @Parameters(
      paramLabel = "<value>",
      arity = "1..*",
      description = "value(s) which execution tree is printed")
  List<String> values;

  @Override
  protected Integer executeCommand(Path projectDir) {
    return createInjector(projectDir, installationDir(), out(), logLevel)
        .getInstance(TreeRunner.class)
        .run(values);
  }
}
