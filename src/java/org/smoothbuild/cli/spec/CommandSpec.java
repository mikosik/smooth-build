package org.smoothbuild.cli.spec;

import net.sourceforge.argparse4j.inf.Subparser;

public interface CommandSpec {
  public void configureParser(Subparser parser);
}
