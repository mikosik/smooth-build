package org.smoothbuild.cli;

import picocli.CommandLine.Command;

@Command(
    usageHelpAutoWidth= true,
    synopsisHeading = "@|bold,underline Usage:|@%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n",
    commandListHeading = "%n@|bold,underline Commands:|@%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n"
)
public class StandardOptions {
}
