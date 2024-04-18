package org.smoothbuild.cli.command;

import java.io.PrintWriter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(
    usageHelpAutoWidth = true,
    synopsisHeading = "@|bold,underline Usage:|@%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n",
    commandListHeading = "%n@|bold,underline Commands:|@%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n")
public class ACommand {
  @Spec
  protected CommandSpec spec;

  protected PrintWriter out() {
    return spec.commandLine().getOut();
  }
}
