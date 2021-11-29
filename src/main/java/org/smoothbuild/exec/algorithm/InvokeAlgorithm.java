package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.invokeAlgorithmHash;
import static org.smoothbuild.exec.base.MessageStruct.containsErrors;
import static org.smoothbuild.util.Strings.q;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.plugin.NativeApi;

public class InvokeAlgorithm extends Algorithm {
  private final NatFuncH natFuncH;
  private final String extendedName;
  private final MethodLoader methodLoader;

  public InvokeAlgorithm(TypeH outputType, String extendedName, NatFuncH natFuncH,
      MethodLoader methodLoader) {
    super(outputType, natFuncH.isPure().toJ());
    this.extendedName = extendedName;
    this.methodLoader = methodLoader;
    this.natFuncH = natFuncH;
  }

  @Override
  public Hash hash() {
    return invokeAlgorithmHash(natFuncH);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    var method = methodLoader.load(extendedName, natFuncH);
    try {
      var result = (ValueH) method.invoke(null, createArgs(nativeApi, input.vals()));
      if (result == null) {
        if (!containsErrors(nativeApi.messages())) {
          nativeApi.log().error(q(extendedName)
              + " has faulty native implementation: it returned `null` but logged no error.");
        }
        return new Output(null, nativeApi.messages());
      }
      if (!outputType().equals(result.spec())) {
        nativeApi.log().error(q(extendedName)
            + " has faulty native implementation: Its declared result type == "
            + outputType().q()
            + " but it returned object with type == " + result.spec().q() + ".");
        return new Output(null, nativeApi.messages());
      }
      return new Output(result, nativeApi.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new NativeCallException(q(extendedName)
          + " threw java exception from its native code.", e.getCause());
    }
  }

  private static Object[] createArgs(NativeApi nativeApi, List<ValueH> args) {
    Object[] nativeArgs = new Object[1 + args.size()];
    nativeArgs[0] = nativeApi;
    for (int i = 0; i < args.size(); i++) {
      nativeArgs[i + 1] = args.get(i);
    }
    return nativeArgs;
  }
}
