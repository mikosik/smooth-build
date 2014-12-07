package org.smoothbuild.cli.handle;

import static org.smoothbuild.cli.spec.BuildSpec.FUNCTIONS_ARG;

import java.util.List;

import javax.inject.Inject;

import net.sourceforge.argparse4j.inf.Namespace;

import org.smoothbuild.cli.work.BuildWorker;

public class Build implements Handler {
  private final BuildWorker worker;

  @Inject
  public Build(BuildWorker worker) {
    this.worker = worker;
  }

  @Override
  public boolean run(Namespace namespace) {
    return worker.run((List<String>) namespace.get(FUNCTIONS_ARG));
  }
}
