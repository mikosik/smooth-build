package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.message.Messages.containsErrors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.NativeCallExpression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Value;
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

  public Expression createCallExpression(List<Expression> args, boolean isGenerated,
      CodeLocation codeLocation) {
    return new NativeCallExpression(this, isGenerated, codeLocation, args);
  }

  public Value invoke(ContainerImpl container, List<Value> arguments) {
    try {
      Value result = (Value) method.invoke(null, createArguments(container, arguments));
      if (result == null && !containsErrors(container.messages())) {
        container.log(new ErrorMessage("Native function " + name()
            + " has faulty implementation: it returned 'null' but logged no error."));
      }
      return result;
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof Message) {
        container.log((Message) cause);
      } else {
        throw new RuntimeException(e);
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
