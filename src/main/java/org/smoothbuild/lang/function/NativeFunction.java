package org.smoothbuild.lang.function;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.lang.message.Messages.containsErrors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.NativeCallExpression;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunction extends Function {
  private final Native nativ;
  private final HashCode hash;
  private final boolean isCacheable;

  public NativeFunction(Native nativ, Signature signature, Location location, boolean isCacheable,
      HashCode hash) {
    super(signature, location);
    this.nativ = nativ;
    this.hash = hash;
    this.isCacheable = isCacheable;
  }

  public HashCode hash() {
    return hash;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  @Override
  public Expression createCallExpression(boolean isGenerated, Location location) {
    return new NativeCallExpression(this, isGenerated, location);
  }

  public Output invoke(Container container, List<Value> arguments) {
    try {
      Value result = (Value) nativ.method().invoke(null, createArguments(container, arguments));
      if (result == null) {
        return nullOutput(container);
      }
      if (!type().equals(result.type())) {
        container.log().error("Function " + name()
            + " has faulty native implementation: Its result type is " + type().name()
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
        container.log().error("Function " + name()
            + " threw java exception from its native code:\n"
            + getStackTraceAsString(cause));
        return new Output(null, container.messages(), false);
      }
    }
  }

  private Output nullOutput(Container container) {
    if (!containsErrors(container.messages())) {
      container.log().error("Function " + name()
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
