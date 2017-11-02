package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.message.Messages.containsErrors;
import static org.smoothbuild.task.base.Evaluator.nativeCallEvaluator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunction extends AbstractFunction {
  private final Native nativ;
  private final HashCode hash;
  private final boolean isCacheable;

  public NativeFunction(Native nativ, Signature signature, boolean isCacheable, HashCode hash) {
    super(signature);
    this.nativ = nativ;
    this.hash = hash;
    this.isCacheable = isCacheable;
  }

  public Method method() {
    return nativ.method();
  }

  public HashCode hash() {
    return hash;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  public Expression createCallExpression(List<Expression> args, boolean isGenerated,
      Location location) {
    return new Expression(type(), args, location) {
      public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
        return nativeCallEvaluator(NativeFunction.this, isGenerated, location,
            createDependenciesEvaluator(valuesDb, scope));
      }
    };
  }

  public Value invoke(ContainerImpl container, List<Value> arguments) {
    try {
      Value result = (Value) nativ.method().invoke(null, createArguments(container, arguments));
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
        throw new RuntimeException(cause);
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
