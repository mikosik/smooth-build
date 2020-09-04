package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.base.CreateInjector.createInjector;

import java.nio.file.Path;
import java.util.List;

import org.smoothbuild.cli.base.ExclusiveCommand;
import org.smoothbuild.run.PlanRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = PlanCommand.NAME,
    description = "Print execution plan for specified value(s)"
)
public class PlanCommand extends ExclusiveCommand {
  public static final String NAME = "plan";

  @Parameters(
      paramLabel = "<value>",
      arity = "1..*",
      description = "value(s) which execution plan is printed")
  List<String> values;

  @Override
  protected Integer executeCommand(Path projectDir) {
    return createInjector(projectDir, installationDir(), out(), logLevel)
        .getInstance(PlanRunner.class)
        .run(values);
  }
}
