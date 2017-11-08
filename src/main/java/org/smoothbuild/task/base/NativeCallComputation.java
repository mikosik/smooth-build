package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.ComputationHashes.nativeCallComputationHash;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class NativeCallComputation implements Computation {
  private final NativeFunction function;

  public NativeCallComputation(NativeFunction function) {
    this.function = function;
  }

  @Override
  public HashCode hash() {
    return nativeCallComputationHash(function);
  }

  @Override
  public Type resultType() {
    return function.type();
  }

  @Override
  public Output execute(Input input, ContainerImpl container) {
    Value result = function.invoke(container, input.values());
    return new Output(result, container.messages());
  }
}
