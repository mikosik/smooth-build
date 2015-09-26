package org.smoothbuild.cli.handle;

import javax.inject.Inject;

import net.sourceforge.argparse4j.inf.Namespace;

import org.smoothbuild.cli.work.CleanWorker;

public class Clean implements Handler {
  private final CleanWorker worker;

  @Inject
  public Clean(CleanWorker worker) {
    this.worker = worker;
  }

  @Override
  public int run(Namespace namespace) {
    return worker.run();
  }
}
