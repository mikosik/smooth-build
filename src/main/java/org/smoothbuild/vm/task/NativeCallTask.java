package org.smoothbuild.vm.task;

import static org.smoothbuild.run.eval.MessageStruct.containsErrors;
import static org.smoothbuild.util.Strings.q;
import static org.smoothbuild.vm.execute.TaskKind.CALL;
import static org.smoothbuild.vm.task.TaskHashes.invokeTaskHash;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.plugin.NativeApi;

public class NativeCallTask extends Task {
  private final NatFuncB natFuncB;
  private final String name;
  private final NativeMethodLoader nativeMethodLoader;

  public NativeCallTask(TypeB outputT, String name, NatFuncB natFunc,
      NativeMethodLoader methodLoader, ExprInfo exprInfo) {
    super(outputT, CALL, exprInfo, natFunc.isPure().toJ());
    this.name = name;
    this.nativeMethodLoader = methodLoader;
    this.natFuncB = natFunc;
  }

  @Override
  public Hash hash() {
    return invokeTaskHash(natFuncB);
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    return nativeMethodLoader.load(name, natFuncB)
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
    if (!outputT().equals(result.type())) {
      logFaultyImplementationError(nativeApi, "Its declared result type == "
          + outputT().q() + " but it returned object with type == " + result.cat().q() + ".");
      return new Output(null, nativeApi.messages());
    }
    if (hasErrors) {
      logFaultyImplementationError(nativeApi, "It returned non-null value but logged error.");
      return new Output(null, nativeApi.messages());
    }
    return new Output(result, nativeApi.messages());
  }

  private ValB invoke(Method method, TupleB args, NativeApi nativeApi) {
    try {
      return (ValB) method.invoke(null, new Object[] {nativeApi, args});
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
}
