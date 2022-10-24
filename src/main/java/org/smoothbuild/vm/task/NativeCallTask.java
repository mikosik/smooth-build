package org.smoothbuild.vm.task;

import static org.smoothbuild.util.Strings.q;
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
import org.smoothbuild.plugin.NativeApi;
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
        .orElse(e -> logErrorAndReturnNullOutput(container, e));
  }

  private Output invokeMethod(Method method, TupleB args, Container container) {
    var result = invoke(method, args, container);
    var hasErrors = container.containsErrorOrAbove();
    if (result == null) {
      if (!hasErrors) {
        logFaultyImplementationError(container, "It returned `null` but logged no error.");
      }
      return new Output(null, container.messages());
    }
    if (!outputT().equals(result.evalT())) {
      logFaultyImplementationError(container, "Its declared result type == "
          + outputT().q() + " but it returned object with type == " + result.category().q() + ".");
      return new Output(null, container.messages());
    }
    if (hasErrors) {
      logFaultyImplementationError(container, "It returned non-null value but logged error.");
      return new Output(null, container.messages());
    }
    return new Output(result, container.messages());
  }

  private InstB invoke(Method method, TupleB args, NativeApi nativeApi) {
    try {
      return (InstB) method.invoke(null, new Object[] {nativeApi, args});
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(
          q(name) + " threw java exception from its native code.", e.getCause());
    }
  }

  private void logFaultyImplementationError(NativeApi nativeApi, String message) {
    nativeApi.log().error(q(name) + " has faulty native implementation: " + message);
  }

  private static Output logErrorAndReturnNullOutput(NativeApi nativeApi, String message) {
    nativeApi.log().error(message);
    return new Output(null, nativeApi.messages());
  }

  public NatFuncB natFunc() {
    return natFunc;
  }
}
