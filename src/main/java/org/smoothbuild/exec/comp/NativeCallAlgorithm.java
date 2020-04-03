package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.nativeCallAlgorithmHash;
import static org.smoothbuild.lang.object.base.Messages.containsErrors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;

public class NativeCallAlgorithm implements Algorithm {
  private final ConcreteType type;
  private final NativeFunction function;

  public NativeCallAlgorithm(ConcreteType type, NativeFunction function) {
    this.type = type;
    this.function = function;
  }

  @Override
  public String name() {
    return function.name();
  }

  @Override
  public Hash hash() {
    return nativeCallAlgorithmHash(function);
  }

  @Override
  public ConcreteType type() {
    return type;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws ComputationException {
    try {
      SObject result = (SObject) function.nativ().method()
          .invoke(null, createArguments(nativeApi, input.objects()));
      if (result == null) {
        return nullOutput(nativeApi);
      }
      if (!type.equals(result.type())) {
        nativeApi.log().error("Function " + function.name()
            + " has faulty native implementation: Its actual result type is " + type.name()
            + " but it returned object of type " + result.type().name() + ".");
        return new Output(null, nativeApi.messages());
      }
      return new Output(result, nativeApi.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof AbortException) {
        return nullOutput(nativeApi);
      } else {
        throw new ComputationException(
            "Function " + function.name() + " threw java exception from its native code.", cause);
      }
    }
  }

  private Output nullOutput(NativeApi nativeApi) {
    if (!containsErrors(nativeApi.messages())) {
      nativeApi.log().error("Function " + function.name()
          + " has faulty native implementation: it returned 'null' but logged no error.");
    }
    return new Output(null, nativeApi.messages());
  }

  private static Object[] createArguments(NativeApi nativeApi, List<SObject> arguments) {
    Object[] nativeArguments = new Object[1 + arguments.size()];
    nativeArguments[0] = nativeApi;
    for (int i = 0; i < arguments.size(); i++) {
      nativeArguments[i + 1] = arguments.get(i);
    }
    return nativeArguments;
  }
}
