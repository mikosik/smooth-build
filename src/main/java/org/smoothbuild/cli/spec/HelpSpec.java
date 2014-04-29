package org.smoothbuild.cli.spec;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.Subparser;

public class HelpSpec implements CommandSpec {
  public static final String ARGUMENT_NAME = "help-argument";

  private static final String DESCRIPTION = "print help about given command";

  @Override
  public void configureParser(Subparser parser) {
    parser.help(DESCRIPTION);

    Argument argument = parser.addArgument(ARGUMENT_NAME);
    argument.metavar("<command>");
    argument.help("command for which help is printed");
    argument.nargs("?");
  }
}
