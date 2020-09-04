package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.base.MessageStruct.containsErrors;

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
  private final Spec spec;
  private final Native nativ;

  public CallNativeAlgorithm(Spec spec, Native nativ) {
    this.spec = spec;
    this.nativ = nativ;
  }

  @Override
  public Hash hash() {
    return callNativeAlgorithmHash(nativ);
  }

  @Override
  public Spec outputSpec() {
    return spec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    try {
      Obj result = (Obj) nativ.method()
          .invoke(null, createArguments(nativeApi, input.objects()));
      if (result == null) {
        if (!containsErrors(nativeApi.messages())) {
          nativeApi.log().error("`" + nativ.name()
              + "` has faulty native implementation: it returned `null` but logged no error.");
        }
        return new Output(null, nativeApi.messages());
      }
      if (!spec.equals(result.spec())) {
        nativeApi.log().error("`" + nativ.name()
            + "` has faulty native implementation: Its declared result spec == " + spec.name()
            + " but it returned object with spec == " + result.spec().name() + ".");
        return new Output(null, nativeApi.messages());
      }
      return new Output(result, nativeApi.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new NativeCallException("`" + nativ.name()
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
