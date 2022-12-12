package org.smoothbuild.vm.task;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.vm.task.Purity.IMPURE;
import static org.smoothbuild.vm.task.Purity.PURE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.TraceB;

public final class InvokeTask extends Task {
  private final NativeFuncB nativeFuncB;
  private final NativeMethodLoader nativeMethodLoader;

  public InvokeTask(
      CallB callB, NativeFuncB nativeFuncB, NativeMethodLoader methodLoader, TraceB trace) {
    super(callB, trace, nativeFuncB.isPure().toJ() ? PURE : IMPURE);
    this.nativeMethodLoader = methodLoader;
    this.nativeFuncB = nativeFuncB;
  }

  @Override
  public Output run(TupleB input, Container container) {
    return nativeMethodLoader.load(nativeFuncB)
        .map(m -> invokeMethod(m, input, container))
        .orElse(e -> logFatalAndReturnNullOutput(container, e));
  }

  private Output invokeMethod(Method method, TupleB args, Container container) {
    ValueB result = null;
    try {
      result = (ValueB) method.invoke(null, new Object[] {container, args});
    } catch (IllegalAccessException e) {
      reportExceptionAsFatal(container, "Cannot invoke native method", e);
    } catch (InvocationTargetException e) {
      reportExceptionAsFatal(container, "Native code thrown exception", e.getCause());
    } catch (Throwable t) {
      reportExceptionAsFatal(container, "Exception when invoking native method", t);
    }
    return buildOutput(container, result);
  }

  private static void reportExceptionAsFatal(Container container, String message,
      Throwable throwable) {
    container.log().fatal(message + ":\n" + getStackTraceAsString(throwable));
  }

  private Output buildOutput(Container container, ValueB result) {
    var hasErrors = container.containsErrorOrAbove();
    if (result == null) {
      if (!hasErrors) {
        logFaultyImplementation(container, "It returned `null` but logged no error.");
      }
      return new Output(null, container.messages());
    }
    if (!outputT().equals(result.evalT())) {
      logFaultyImplementation(container, "Its declared result type == "
          + outputT().q() + " but it returned object with type == " + result.category().q() + ".");
      return new Output(null, container.messages());
    }
    if (hasErrors) {
      logFaultyImplementation(container, "It returned non-null value but logged error.");
      return new Output(null, container.messages());
    }
    return new Output(result, container.messages());
  }

  private void logFaultyImplementation(Container container, String message) {
    container.log().fatal("Faulty native implementation: " + message);
  }

  private static Output logFatalAndReturnNullOutput(Container container, String message) {
    container.log().fatal(message);
    return new Output(null, container.messages());
  }

  public NativeFuncB nativeFunc() {
    return nativeFuncB;
  }
}
