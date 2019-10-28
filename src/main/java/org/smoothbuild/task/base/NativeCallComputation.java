package org.smoothbuild.task.base;

import static org.smoothbuild.lang.object.base.Messages.containsErrors;
import static org.smoothbuild.task.base.ComputationHashes.nativeCallComputationHash;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.task.exec.Container;

public class NativeCallComputation implements Computation {
  private final ConcreteType type;
  private final NativeFunction function;

  public NativeCallComputation(ConcreteType type, NativeFunction function) {
    this.type = type;
    this.function = function;
  }

  @Override
  public Hash hash() {
    return nativeCallComputationHash(function);
  }

  @Override
  public ConcreteType type() {
    return type;
  }

  @Override
  public Output execute(Input input, Container container) throws ComputationException {
    try {
      SObject result = (SObject) function.nativ().method()
          .invoke(null, createArguments(container, input.objects()));
      if (result == null) {
        return nullOutput(container);
      }
      if (!type.equals(result.type())) {
        container.log().error("Function " + function.name()
            + " has faulty native implementation: Its actual result type is " + type.name()
            + " but it returned object of type " + result.type().name() + ".");
        return new Output(null, container.messages());
      }
      return new Output(result, container.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof AbortException) {
        return nullOutput(container);
      } else {
        throw new ComputationException(
            "Function " + function.name() + " threw java exception from its native code.", cause);
      }
    }
  }

  private Output nullOutput(Container container) {
    if (!containsErrors(container.messages())) {
      container.log().error("Function " + function.name()
          + " has faulty native implementation: it returned 'null' but logged no error.");
    }
    return new Output(null, container.messages());
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
