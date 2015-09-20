package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.AlgorithmHashes.nativeCallAlgorithmHash;

import java.util.List;

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

  @Override
  public HashCode hash() {
    return nativeCallAlgorithmHash(function);
  }

  @Override
  public Type resultType() {
    return function.type();
  }

  @Override
  public Output execute(Input input, ContainerImpl container) {
    Value result = function.invoke(container, calculateArguments(input));
    return new Output(result, container.messages());
  }

  private List<Value> calculateArguments(Input input) {
    return input.values();
  }
}
