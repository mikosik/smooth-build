package org.smoothbuild.virtualmachine.evaluate.compute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.common.init.Initializable;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.tuple.Tuple0;

public class ComputationCacheInitializer implements Initializable {
  private final ComputationCache computationCache;

  @Inject
  public ComputationCacheInitializer(ComputationCache computationCache) {
    this.computationCache = computationCache;
  }

  @Override
  public Output<Tuple0> execute() {
    var label = INITIALIZE_LABEL.append("computationCache");
    try {
      computationCache.initialize();
      return output(tuple(), label, list());
    } catch (IOException e) {
      var fatal = fatal("Initializing ComputationCache failed with exception:", e);
      return output(label, list(fatal));
    }
  }
}
