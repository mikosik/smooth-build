package org.smoothbuild.vm.task;

import static org.smoothbuild.run.eval.MessageStruct.containsErrors;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.vm.execute.TaskKind.CALL;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.plugin.NativeApi;

public final class NativeCallTask extends ExecutableTask {
  private final NatFuncB natFunc;
  private final String name;
  private final NativeMethodLoader nativeMethodLoader;

  public NativeCallTask(TypeB outputT, String name, NatFuncB natFunc,
      NativeMethodLoader methodLoader, TagLoc tagLoc, TraceS trace) {
    super(outputT, CALL, tagLoc, trace, natFunc.isPure().toJ());
    this.name = name;
    this.nativeMethodLoader = methodLoader;
    this.natFunc = natFunc;
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    return nativeMethodLoader.load(name, natFunc)
        .map(m -> invokeMethod(m, input, nativeApi))
        .orElse(e -> logErrorAndReturnNullOutput(nativeApi, e));
  }

  private Output invokeMethod(Method method, TupleB args, NativeApi nativeApi) {
    var result = invoke(method, args, nativeApi);
    var hasErrors = containsErrors(nativeApi.messages());
    if (result == null) {
      if (!hasErrors) {
        logFaultyImplementationError(nativeApi, "It returned `null` but logged no error.");
      }
      return new Output(null, nativeApi.messages());
    }
    if (!outputT().equals(result.evalT())) {
      logFaultyImplementationError(nativeApi, "Its declared result type == "
          + outputT().q() + " but it returned object with type == " + result.category().q() + ".");
      return new Output(null, nativeApi.messages());
    }
    if (hasErrors) {
      logFaultyImplementationError(nativeApi, "It returned non-null value but logged error.");
      return new Output(null, nativeApi.messages());
    }
    return new Output(result, nativeApi.messages());
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
