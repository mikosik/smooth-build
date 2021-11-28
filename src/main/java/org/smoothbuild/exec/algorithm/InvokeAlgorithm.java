package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.invokeAlgorithmHash;
import static org.smoothbuild.exec.base.MessageStruct.containsErrors;
import static org.smoothbuild.util.Strings.q;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.NativeFunctionH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.plugin.NativeApi;

public class InvokeAlgorithm extends Algorithm {
  private final NativeFunctionH nativeFunctionH;
  private final String extendedName;
  private final MethodLoader methodLoader;

  public InvokeAlgorithm(TypeHV outputType, String extendedName, NativeFunctionH nativeFunctionH,
      MethodLoader methodLoader) {
    super(outputType, nativeFunctionH.isPure().jValue());
    this.extendedName = extendedName;
    this.methodLoader = methodLoader;
    this.nativeFunctionH = nativeFunctionH;
  }

  @Override
  public Hash hash() {
    return invokeAlgorithmHash(nativeFunctionH);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    var method = methodLoader.load(extendedName, nativeFunctionH);
    try {
      var result = (ValueH) method.invoke(null, createArguments(nativeApi, input.vals()));
      if (result == null) {
        if (!containsErrors(nativeApi.messages())) {
          nativeApi.log().error(q(extendedName)
              + " has faulty native implementation: it returned `null` but logged no error.");
        }
        return new Output(null, nativeApi.messages());
      }
      if (!outputType().equals(result.type())) {
        nativeApi.log().error(q(extendedName)
            + " has faulty native implementation: Its declared result type == "
            + outputType().q()
            + " but it returned object with type == " + result.type().q() + ".");
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

  private static Object[] createArguments(NativeApi nativeApi, List<ValueH> arguments) {
    Object[] nativeArguments = new Object[1 + arguments.size()];
    nativeArguments[0] = nativeApi;
    for (int i = 0; i < arguments.size(); i++) {
      nativeArguments[i + 1] = arguments.get(i);
    }
    return nativeArguments;
  }
}