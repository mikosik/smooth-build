package org.smoothbuild.cli.command;

import org.smoothbuild.cli.base.FormattedHeadings;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

@Command(
    name = "smooth",
    description = "smooth-build is a build tool with strongly and statically typed, " +
        "purely functional language. It features fine-grained, aggressive caching " +
        "that will make sure no computation happens twice on the same machine, " +
        "decreasing build times significantly.\n" +
        "More info at https://github.com/mikosik/smooth-build/blob/master/doc/tutorial.md",
    subcommands = {
        BuildCommand.class,
        CleanCommand.class,
        HelpCommand.class,
        ListCommand.class,
        TreeCommand.class,
        VersionCommand.class
    },
    synopsisSubcommandLabel = "COMMAND" // to avoid default [COMMAND] because COMMAND is mandatory
)
public class SmoothCommand extends FormattedHeadings implements Runnable  {
  @Override
  public void run() {
    new CommandLine(this).usage(System.out);
  }
}
