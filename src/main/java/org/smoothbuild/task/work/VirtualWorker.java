package org.smoothbuild.task.work;

import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.compute.IdentityAlgorithm;

public class VirtualWorker extends TaskWorker {
  public VirtualWorker(DefinedFunction function, CodeLocation codeLocation) {
    super(new IdentityAlgorithm(function.type()), WorkerHashes.workerHash(VirtualWorker.class),
        function.name().value(), false, true, codeLocation);
  }
}
