package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke.ARGUMENTS_INDEX;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke.METHOD_INDEX;
import static org.smoothbuild.virtualmachine.evaluate.base.Purity.IMPURE;
import static org.smoothbuild.virtualmachine.evaluate.base.Purity.PURE;
import static org.smoothbuild.virtualmachine.evaluate.plugin.BOutput.bOutput;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMethod;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.base.Purity;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

public final class InvokeEvaluator extends OperationEvaluator {
  public InvokeEvaluator(BInvoke invoke, Trace trace) {
    super(invoke, trace);
  }

  @Override
  public Purity purity(BTuple subExprValues) throws BytecodeException {
    var isPure = ((BBool) subExprValues.get(BInvoke.IS_PURE_INDEX)).toJavaBoolean();
    return isPure ? PURE : IMPURE;
  }

  @Override
  public BOutput evaluate(BTuple subExprValues, Container container) throws IOException {
    return container
        .nativeMethodLoader()
        .load(new BMethod((BTuple) subExprValues.get(METHOD_INDEX)))
        .mapOk(m -> invokeMethod(m, subExprValues.get(ARGUMENTS_INDEX), container))
        .ifErr(e -> container.log().fatal(e))
        .okOrGet(() -> bOutput(container.messages()));
  }

  private BOutput invokeMethod(Method method, BValue arguments, Container container)
      throws BytecodeException {
    BValue result = null;
    try {
      result = (BValue) method.invoke(null, new Object[] {container, arguments});
    } catch (IllegalAccessException e) {
      reportExceptionAsFatal(container, "Cannot invoke native method", e);
    } catch (InvocationTargetException e) {
      var cause = e.getCause();
      reportExceptionAsFatal(container, "Native code thrown exception", cause == null ? e : cause);
    } catch (Throwable t) {
      reportExceptionAsFatal(container, "Exception when invoking native method", t);
    }
    return buildOutput(container, result);
  }

  private static void reportExceptionAsFatal(
      Container container, String message, Throwable throwable) throws BytecodeException {
    container.log().fatal(message + ":\n" + getStackTraceAsString(throwable));
  }

  private BOutput buildOutput(Container container, @Nullable BValue result)
      throws BytecodeException {
    var hasErrors = container.containsErrorOrAbove();
    if (result == null) {
      if (!hasErrors) {
        logFaultyImplementation(container, "It returned `null` but logged no error.");
      }
      return bOutput(container.messages());
    }
    if (!operation().evaluationType().equals(result.evaluationType())) {
      logFaultyImplementation(
          container,
          "Its declared result type == " + operation().evaluationType().q()
              + " but it returned expression with type == " + result.kind().q() + ".");
      return bOutput(container.messages());
    }
    if (hasErrors) {
      logFaultyImplementation(container, "It returned non-null value but logged error.");
      return bOutput(container.messages());
    }
    return bOutput(result, container.messages());
  }

  private void logFaultyImplementation(Container container, String message)
      throws BytecodeException {
    container.log().fatal("Faulty native implementation: " + message);
  }
}
