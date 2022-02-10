package org.smoothbuild.vm.job.algorithm;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.smoothbuild.run.eval.MessageStruct.containsErrors;
import static org.smoothbuild.util.Strings.q;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.java.ClassLoaderProv;
import org.smoothbuild.vm.java.MethodLoader;
import org.smoothbuild.vm.java.MethodLoaderExc;

public class InvokeAlgorithm extends Algorithm {
  private final MethodB methodB;
  private final String name;
  private final MethodLoader methodLoader;

  public InvokeAlgorithm(TypeB outputT, String name, MethodB method, MethodLoader methodLoader) {
    super(outputT, method.isPure().toJ());
    this.name = name;
    this.methodLoader = methodLoader;
    this.methodB = method;
  }

  @Override
  public Hash hash() {
    return AlgorithmHashes.invokeAlgorithmHash(methodB);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    try {
      return runImpl(input, nativeApi);
    } catch (InvokeExc e) {
      nativeApi.log().error(e.getMessage());
      return new Output(null, nativeApi.messages());
    }
  }

  private Output runImpl(Input input, NativeApi nativeApi) throws InvokeExc {
    var classLoaderProv = new ClassLoaderProv(getSystemClassLoader(), nativeApi);
    var method = loadMethod(classLoaderProv);
    var result = invoke(method, input, nativeApi);
    var hasErrors = containsErrors(nativeApi.messages());
    if (result == null) {
      if (!hasErrors) {
        logFaultyImplementationError(nativeApi, "It returned `null` but logged no error.");
      }
      return new Output(null, nativeApi.messages());
    }
    if (!outputT().equals(result.cat())) {
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

  private void logFaultyImplementationError(NativeApi nativeApi, String message) {
    nativeApi.log().error(q(name) + " has faulty native implementation: " + message);
  }

  private Method loadMethod(ClassLoaderProv classLoaderProv) throws InvokeExc {
    try {
      return methodLoader.load(name, methodB, classLoaderProv);
    } catch (MethodLoaderExc e) {
      throw new InvokeExc(e);
    }
  }

  private ValB invoke(Method method, Input input, NativeApi nativeApi) {
    try {
      return (ValB) method.invoke(null, createArgs(nativeApi, input.vals()));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(
          q(name) + " threw java exception from its native code.", e.getCause());
    }
  }

  private static Object[] createArgs(NativeApi nativeApi, List<ValB> args) {
    Object[] nativeArgs = new Object[1 + args.size()];
    nativeArgs[0] = nativeApi;
    for (int i = 0; i < args.size(); i++) {
      nativeArgs[i + 1] = args.get(i);
    }
    return nativeArgs;
  }

  private static class InvokeExc extends Exception {
    public InvokeExc(Throwable e) {
      super(e.getMessage(), e);
    }
  }
}
