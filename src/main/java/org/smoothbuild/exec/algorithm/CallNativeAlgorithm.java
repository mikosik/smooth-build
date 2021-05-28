package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.base.MessageTuple.containsErrors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.nativ.Native;
import org.smoothbuild.plugin.NativeApi;

public class CallNativeAlgorithm implements Algorithm {
  private final Spec outputSpec;
  private final String referencableName;
  private final Native nativ;

  public CallNativeAlgorithm(Spec outputSpec, String referencableName, Native nativ) {
    this.outputSpec = outputSpec;
    this.referencableName = referencableName;
    this.nativ = nativ;
  }

  @Override
  public Hash hash() {
    return callNativeAlgorithmHash(nativ);
  }

  @Override
  public Spec outputSpec() {
    return outputSpec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    try {
      Obj result = (Obj) nativ.method()
          .invoke(null, createArguments(nativeApi, input.objects()));
      if (result == null) {
        if (!containsErrors(nativeApi.messages())) {
          nativeApi.log().error("`" + referencableName
              + "` has faulty native implementation: it returned `null` but logged no error.");
        }
        return new Output(null, nativeApi.messages());
      }
      if (!outputSpec.equals(result.spec())) {
        nativeApi.log().error("`" + referencableName
            + "` has faulty native implementation: Its declared result spec == " + outputSpec.name()
            + " but it returned object with spec == " + result.spec().name() + ".");
        return new Output(null, nativeApi.messages());
      }
      return new Output(result, nativeApi.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new NativeCallException("`" + referencableName
          + "` threw java exception from its native code.", e.getCause());
    }
  }

  private static Object[] createArguments(NativeApi nativeApi, List<Obj> arguments) {
    Object[] nativeArguments = new Object[1 + arguments.size()];
    nativeArguments[0] = nativeApi;
    for (int i = 0; i < arguments.size(); i++) {
      nativeArguments[i + 1] = arguments.get(i);
    }
    return nativeArguments;
  }
}
