package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.invokeAlgorithmHash;
import static org.smoothbuild.exec.base.MessageStruct.containsErrors;
import static org.smoothbuild.util.Strings.q;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.plugin.NativeApi;

public class InvokeAlgorithm extends Algorithm {
  private final InvokeH invokeH;
  private final String extendedName;
  private final MethodLoader methodLoader;

  public InvokeAlgorithm(TypeH outputT, String extendedName, InvokeH invokeH,
      MethodLoader methodLoader) {
    super(outputT, invokeH.isPure().toJ());
    this.extendedName = extendedName;
    this.methodLoader = methodLoader;
    this.invokeH = invokeH;
  }

  @Override
  public Hash hash() {
    return invokeAlgorithmHash(invokeH);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    var method = methodLoader.load(extendedName, invokeH);
    try {
      var result = (ValH) method.invoke(null, createArgs(nativeApi, input.vals()));
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

  private static Object[] createArgs(NativeApi nativeApi, List<ValH> args) {
    Object[] nativeArgs = new Object[1 + args.size()];
    nativeArgs[0] = nativeApi;
    for (int i = 0; i < args.size(); i++) {
      nativeArgs[i + 1] = args.get(i);
    }
    return nativeArgs;
  }
}
