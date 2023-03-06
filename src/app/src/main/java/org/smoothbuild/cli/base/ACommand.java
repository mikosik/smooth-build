package org.smoothbuild.cli.base;

import static org.smoothbuild.install.DetectInstallationDir.detectInstallationDir;

import java.io.PrintWriter;
import java.nio.file.Path;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Command(
    usageHelpAutoWidth= true,
    synopsisHeading = "@|bold,underline Usage:|@%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n",
    commandListHeading = "%n@|bold,underline Commands:|@%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n"
)
public class ACommand {
  @Spec
  protected CommandSpec spec;

  @Option(
      names = { "--INTERNAL-installation-dir" },
      hidden = true
  )
  Path installationDir = null;

  protected Path installationDir() {
    return installationDir == null ? detectInstallationDir() : installationDir;
  }

  protected PrintWriter out() {
    return spec.commandLine().getOut();
  }
}
