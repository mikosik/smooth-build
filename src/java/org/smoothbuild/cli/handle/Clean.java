package org.smoothbuild.cli.handle;

import javax.inject.Inject;

import org.smoothbuild.cli.work.CleanWorker;

import net.sourceforge.argparse4j.inf.Namespace;

public class Clean implements Handler {
  private final CleanWorker worker;

  @Inject
  public Clean(CleanWorker worker) {
    this.worker = worker;
  }

  @Override
  public boolean run(Namespace namespace) {
    return worker.run();
  }
}
