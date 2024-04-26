package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke.ARGUMENTS_INDEX;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke.METHOD_INDEX;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.IMPURE;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.PURE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public final class InvokeStep extends Step {
  public InvokeStep(BInvoke invoke, BTrace trace) {
    super(invoke, trace);
  }

  public static InvokeStep newInvokeStep(BInvoke invoke, BTrace trace) {
    return new InvokeStep(invoke, trace);
  }

  public BInvoke invoke() {
    return ((BInvoke) expr());
  }

  @Override
  public Purity purity(BTuple input) throws BytecodeException {
    var isPure = ((BBool) input.get(BInvoke.IS_PURE_IDX)).toJavaBoolean();
    return isPure ? PURE : IMPURE;
  }

  @Override
  public Output run(BTuple input, Container container) throws BytecodeException {
    return container
        .nativeMethodLoader()
        .load(new BMethod((BTuple) input.get(METHOD_INDEX)))
        .mapRight(m -> invokeMethod(m, input.get(ARGUMENTS_INDEX), container))
        .ifLeft(left -> container.log().fatal(left))
        .rightOrGet(() -> new Output(null, container.messages()));
  }

  private Output invokeMethod(Method method, BValue arguments, Container container)
      throws BytecodeException {
    BValue result = null;
    try {
      result = (BValue) method.invoke(null, new Object[] {container, arguments});
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
              + " but it returned expression with type == " + result.kind().q() + ".");
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
}
