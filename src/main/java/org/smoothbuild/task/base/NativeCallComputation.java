package org.smoothbuild.task.base;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.lang.message.Messages.containsErrors;
import static org.smoothbuild.task.base.ComputationHashes.nativeCallComputationHash;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

public class NativeCallComputation implements Computation {
  private final Type type;
  private final NativeFunction function;

  public NativeCallComputation(Type type, NativeFunction function) {
    this.type = type;
    this.function = function;
  }

  @Override
  public HashCode hash() {
    return nativeCallComputationHash(function);
  }

  @Override
  public Type resultType() {
    return type;
  }

  @Override
  public Output execute(Input input, Container container) {
    try {
      Value result = (Value) function.nativ().method()
          .invoke(null, createArguments(container, input.values()));
      if (result == null) {
        return nullOutput(container);
      }
      if (!function.type().isAssignableFrom(result.type())) {
        container.log().error("Function " + function.name()
            + " has faulty native implementation: Its result type is " + function.type().name()
            + " but it returned value of type " + result.type().name() + ".");
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
        container.log().error("Function " + function.name()
            + " threw java exception from its native code:\n"
            + getStackTraceAsString(cause));
        return new Output(null, container.messages(), false);
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

  private static Object[] createArguments(NativeApi nativeApi, List<Value> arguments) {
    Object[] nativeArguments = new Object[1 + arguments.size()];
    nativeArguments[0] = nativeApi;
    for (int i = 0; i < arguments.size(); i++) {
      nativeArguments[i + 1] = arguments.get(i);
    }
    return nativeArguments;
  }

}
