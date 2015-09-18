package org.smoothbuild.task.work;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.compute.ArrayAlgorithm;

public class ArrayWorker extends TaskWorker {
  public ArrayWorker(ArrayType arrayType, CodeLocation codeLocation) {
    super(new ArrayAlgorithm(arrayType), arrayType.name(), true, true, codeLocation);
  }
}
