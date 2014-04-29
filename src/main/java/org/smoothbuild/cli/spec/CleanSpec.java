package org.smoothbuild.cli.spec;

import net.sourceforge.argparse4j.inf.Subparser;

public class CleanSpec implements CommandSpec {
  private static final String DESCRIPTION = "remove all cached artifacts";

  @Override
  public void configureParser(Subparser parser) {
    parser.help(DESCRIPTION);
  }
}
