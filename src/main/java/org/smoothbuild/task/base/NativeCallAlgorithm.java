package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.AlgorithmHashes.nativeCallAlgorithmHash;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class NativeCallAlgorithm implements Algorithm {
  private final NativeFunction function;

  public NativeCallAlgorithm(NativeFunction function) {
    this.function = function;
  }

  public HashCode hash() {
    return nativeCallAlgorithmHash(function);
  }

  public Type resultType() {
    return function.type();
  }

  public Output execute(Input input, ContainerImpl container) {
    Value result = function.invoke(container, input.values());
    return new Output(result, container.messages());
  }
}
