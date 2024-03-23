package org.smoothbuild.virtualmachine.evaluate.task;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.virtualmachine.evaluate.task.Purity.IMPURE;
import static org.smoothbuild.virtualmachine.evaluate.task.Purity.PURE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public final class InvokeTask extends Task {
  private final BNativeFunc nativeFunc;

  public InvokeTask(BCall call, BNativeFunc nativeFunc, BTrace trace) throws BytecodeException {
    super(call, trace, nativeFunc.isPure().toJavaBoolean() ? PURE : IMPURE);
    this.nativeFunc = nativeFunc;
  }

  @Override
  public Output run(BTuple input, Container container) throws BytecodeException {
    return container
        .nativeMethodLoader()
        .load(nativeFunc)
        .mapRight(m -> invokeMethod(m, input, container))
        .ifLeft(left -> container.log().fatal(left))
        .rightOrGet(() -> new Output(null, container.messages()));
  }

  private Output invokeMethod(Method method, BTuple args, Container container)
      throws BytecodeException {
    BValue result = null;
    try {
      result = (BValue) method.invoke(null, new Object[] {container, args});
    } catch (IllegalAccessException e) {
      reportExceptionAsFatal(container, "Cannot invoke native method", e);
    } catch (InvocationTargetException e) {
      reportExceptionAsFatal(container, "Native code thrown exception", e.getCause());
    } catch (Throwable t) {
      reportExceptionAsFatal(container, "Exception when invoking native method", t);
    }
    return buildOutput(container, result);
  }

  private static void reportExceptionAsFatal(
      Container container, String message, Throwable throwable) throws BytecodeException {
    container.log().fatal(message + ":\n" + getStackTraceAsString(throwable));
  }

  private Output buildOutput(Container container, BValue result) throws BytecodeException {
    var hasErrors = container.containsErrorOrAbove();
    if (result == null) {
      if (!hasErrors) {
        logFaultyImplementation(container, "It returned `null` but logged no error.");
      }
      return new Output(null, container.messages());
    }
    if (!outputType().equals(result.evaluationType())) {
      logFaultyImplementation(
          container,
          "Its declared result type == " + outputType().q()
              + " but it returned object with type == " + result.category().q() + ".");
      return new Output(null, container.messages());
    }
    if (hasErrors) {
      logFaultyImplementation(container, "It returned non-null value but logged error.");
      return new Output(null, container.messages());
    }
    return new Output(result, container.messages());
  }

  private void logFaultyImplementation(Container container, String message)
      throws BytecodeException {
    container.log().fatal("Faulty native implementation: " + message);
  }

  public BNativeFunc nativeFunc() {
    return nativeFunc;
  }
}
