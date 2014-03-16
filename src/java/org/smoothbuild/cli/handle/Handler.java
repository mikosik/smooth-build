package org.smoothbuild.cli.handle;

import net.sourceforge.argparse4j.inf.Namespace;

public interface Handler {
  public boolean run(Namespace namespace);
}
