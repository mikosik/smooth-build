package org.smoothbuild.task.work;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.compute.NativeCallAlgorithm;

public class NativeCallWorker extends TaskWorker {
  public NativeCallWorker(NativeFunction function, boolean isInternal, CodeLocation codeLocation) {
    super(new NativeCallAlgorithm(function), function.name().value(), isInternal, function
        .isCacheable(), codeLocation);
  }
}
