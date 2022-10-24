package org.smoothbuild.vm.task;

import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.util.Throwables.stackTraceToString;
import static org.smoothbuild.vm.execute.TaskKind.CALL;
import static org.smoothbuild.vm.task.Purity.IMPURE;
import static org.smoothbuild.vm.task.Purity.PURE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.vm.compute.Container;

public final class NativeCallTask extends Task {
  private final NatFuncB natFunc;
  private final String name;
  private final NativeMethodLoader nativeMethodLoader;

  public NativeCallTask(TypeB outputT, String name, NatFuncB natFunc,
      NativeMethodLoader methodLoader, TagLoc tagLoc, TraceS trace) {
    super(outputT, CALL, tagLoc, trace, natFunc.isPure().toJ() ? PURE : IMPURE);
    this.name = name;
    this.nativeMethodLoader = methodLoader;
    this.natFunc = natFunc;
  }

  @Override
  public Output run(TupleB input, Container container) {
    return nativeMethodLoader.load(name, natFunc)
        .map(m -> invokeMethod(m, input, container))
        .orElse(e -> logFatalAndReturnNullOutput(container, e));
  }

  private Output invokeMethod(Method method, TupleB args, Container container) {
    InstB result = null;
    try {
      result = (InstB) method.invoke(null, new Object[] {container, args});
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
    container.log().fatal(message + ":\n" + stackTraceToString(throwable));
  }

  private Output buildOutput(Container container, InstB result) {
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
    container.log().fatal(q(name) + " has faulty native implementation: " + message);
  }

  private static Output logFatalAndReturnNullOutput(Container container, String message) {
    container.log().fatal(message);
    return new Output(null, container.messages());
  }

  public NatFuncB natFunc() {
    return natFunc;
  }
}
