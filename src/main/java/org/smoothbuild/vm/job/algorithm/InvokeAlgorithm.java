package org.smoothbuild.vm.job.algorithm;

import static org.smoothbuild.eval.artifact.MessageStruct.containsErrors;
import static org.smoothbuild.util.Strings.q;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.bytecode.obj.val.MethodB;
import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.java.MethodLoader;

public class InvokeAlgorithm extends Algorithm {
  private final MethodB methodB;
  private final String extendedName;
  private final MethodLoader methodLoader;

  public InvokeAlgorithm(TypeB outputT, String extendedName, MethodB method,
      MethodLoader methodLoader) {
    super(outputT, method.isPure().toJ());
    this.extendedName = extendedName;
    this.methodLoader = methodLoader;
    this.methodB = method;
  }

  @Override
  public Hash hash() {
    return AlgorithmHashes.invokeAlgorithmHash(methodB);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    var method = methodLoader.load(extendedName, methodB);
    try {
      var result = (ValB) method.invoke(null, createArgs(nativeApi, input.vals()));
      if (result == null) {
        if (!containsErrors(nativeApi.messages())) {
          nativeApi.log().error(q(extendedName)
              + " has faulty native implementation: it returned `null` but logged no error.");
        }
        return new Output(null, nativeApi.messages());
      }
      if (!outputT().equals(result.cat())) {
        nativeApi.log().error(q(extendedName)
            + " has faulty native implementation: Its declared result type == "
            + outputT().q()
            + " but it returned object with type == " + result.cat().q() + ".");
        return new Output(null, nativeApi.messages());
      }
      return new Output(result, nativeApi.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new NativeCallExc(q(extendedName)
          + " threw java exception from its native code.", e.getCause());
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
}
