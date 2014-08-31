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
    @SuppressWarnings("unchecked")
    List<String> functions = namespace.get(FUNCTIONS_ARG);
    return worker.run(functions);
  }
}
