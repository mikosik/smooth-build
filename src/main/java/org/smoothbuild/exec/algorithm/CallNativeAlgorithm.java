package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.base.MessageStruct.containsErrors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.lang.base.NativeWrapper;
import org.smoothbuild.plugin.NativeApi;

public class CallNativeAlgorithm implements Algorithm {
  private final Spec spec;
  private final NativeWrapper nativeWrapper;

  public CallNativeAlgorithm(Spec spec, NativeWrapper nativeWrapper) {
    this.spec = spec;
    this.nativeWrapper = nativeWrapper;
  }

  @Override
  public Hash hash() {
    return callNativeAlgorithmHash(nativeWrapper.nativ());
  }

  @Override
  public Spec outputSpec() {
    return spec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    try {
      Record result = (Record) nativeWrapper.nativ().method()
          .invoke(null, createArguments(nativeApi, input.records()));
      if (result == null) {
        if (!containsErrors(nativeApi.messages())) {
          nativeApi.log().error("`" + nativeWrapper.name()
              + "` has faulty native implementation: it returned `null` but logged no error.");
        }
        return new Output(null, nativeApi.messages());
      }
      if (!spec.equals(result.spec())) {
        nativeApi.log().error("`" + nativeWrapper.name()
            + "` has faulty native implementation: Its declared result spec == " + spec.name()
            + " but it returned record with spec == " + result.spec().name() + ".");
        return new Output(null, nativeApi.messages());
      }
      return new Output(result, nativeApi.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new NativeCallException("`" + nativeWrapper.name()
          + "` threw java exception from its native code.", e.getCause());
    }
  }

  private static Object[] createArguments(NativeApi nativeApi, List<Record> arguments) {
    Object[] nativeArguments = new Object[1 + arguments.size()];
    nativeArguments[0] = nativeApi;
    for (int i = 0; i < arguments.size(); i++) {
      nativeArguments[i + 1] = arguments.get(i);
    }
    return nativeArguments;
  }
}
