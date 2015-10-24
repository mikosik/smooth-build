package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.message.base.Messages.containsProblems;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.nativ.err.JavaInvocationError;
import org.smoothbuild.lang.function.nativ.err.NullResultError;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunction extends AbstractFunction {
  private final Method method;
  private final HashCode hash;
  private final boolean isCacheable;

  public NativeFunction(Method method, Signature signature, boolean isCacheable, HashCode hash) {
    super(signature);
    this.method = method;
    this.hash = hash;
    this.isCacheable = isCacheable;
  }

  public HashCode hash() {
    return hash;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  public Value invoke(ContainerImpl container, List<Value> arguments) {
    try {
      Value result = (Value) method.invoke(null, createArguments(container, arguments));
      if (result == null && !containsProblems(container.messages())) {
        container.log(new NullResultError(this));
      }
      return result;
    } catch (IllegalAccessException e) {
      container.log(new JavaInvocationError(this, e));
      return null;
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof Message) {
        container.log((Message) cause);
      } else {
        container.log(new JavaInvocationError(this, e));
      }
      return null;
    }
  }

  private static Object[] createArguments(Container container, List<Value> arguments) {
    Object[] nativeArguments = new Object[1 + arguments.size()];
    nativeArguments[0] = container;
    for (int i = 0; i < arguments.size(); i++) {
      nativeArguments[i + 1] = arguments.get(i);
    }
    return nativeArguments;
  }
}
