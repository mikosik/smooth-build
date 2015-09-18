package org.smoothbuild.task.work;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.compute.ConstantAlgorithm;

public class DefaultValueWorker extends TaskWorker {
  public DefaultValueWorker(Type type, Value value, CodeLocation codeLocation) {
    super(new ConstantAlgorithm(value), type.name(), true, true, codeLocation);
  }
}
