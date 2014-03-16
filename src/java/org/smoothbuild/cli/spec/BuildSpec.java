package org.smoothbuild.cli.spec;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.Subparser;

public class BuildSpec implements CommandSpec {
  public static final String FUNCTIONS_ARG = "functions";
  private static final String DESCRIPTION = "build artifacts by running specified functions";

  @Override
  public void configureParser(Subparser parser) {
    parser.help(DESCRIPTION);
    parser.description(DESCRIPTION);

    Argument functionsArg = parser.addArgument(FUNCTIONS_ARG);
    functionsArg.dest(FUNCTIONS_ARG);
    functionsArg.metavar("function");
    functionsArg.nargs("*");
    functionsArg.help("function to invoke");
  }
}
