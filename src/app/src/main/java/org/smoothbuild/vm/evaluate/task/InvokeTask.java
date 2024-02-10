package org.smoothbuild.vm.evaluate.task;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.vm.evaluate.task.Purity.IMPURE;
import static org.smoothbuild.vm.evaluate.task.Purity.PURE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.Container;
import org.smoothbuild.vm.evaluate.execute.TraceB;

public final class InvokeTask extends Task {
  private final NativeFuncB nativeFuncB;

  public InvokeTask(CallB callB, NativeFuncB nativeFuncB, TraceB trace) throws BytecodeException {
    super(callB, trace, nativeFuncB.isPure().toJ() ? PURE : IMPURE);
    this.nativeFuncB = nativeFuncB;
  }

  @Override
  public Output run(TupleB input, Container container) throws BytecodeException {
    return container
        .nativeMethodLoader()
        .load(nativeFuncB)
        .mapRight(m -> invokeMethod(m, input, container))
        .ifLeft(left -> container.log().fatal(left))
        .rightOrGet(() -> new Output(null, container.messages()));
  }

  private Output invokeMethod(Method method, TupleB args, Container container)
      throws BytecodeException {
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

  private static void reportExceptionAsFatal(
      Container container, String message, Throwable throwable) throws BytecodeException {
    container.log().fatal(message + ":\n" + getStackTraceAsString(throwable));
  }

  private Output buildOutput(Container container, ValueB result) throws BytecodeException {
    var hasErrors = container.containsErrorOrAbove();
    if (result == null) {
      if (!hasErrors) {
        logFaultyImplementation(container, "It returned `null` but logged no error.");
      }
      return new Output(null, container.messages());
    }
    if (!outputT().equals(result.evaluationT())) {
      logFaultyImplementation(
          container,
          "Its declared result type == " + outputT().q() + " but it returned object with type == "
              + result.category().q() + ".");
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

  public NativeFuncB nativeFunc() {
    return nativeFuncB;
  }
}
