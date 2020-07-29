package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.record.base.Messages.containsErrors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.plugin.AbortException;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.record.spec.Spec;

public class CallNativeAlgorithm implements Algorithm {
  private final Spec spec;
  private final NativeFunction function;

  public CallNativeAlgorithm(Spec spec, NativeFunction function) {
    this.spec = spec;
    this.function = function;
  }

  @Override
  public Hash hash() {
    return callNativeAlgorithmHash(function);
  }

  @Override
  public Spec type() {
    return spec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    try {
      Record result = (Record) function.nativ().method()
          .invoke(null, createArguments(nativeApi, input.records()));
      if (result == null) {
        return nullOutput(nativeApi);
      }
      if (!spec.equals(result.spec())) {
        nativeApi.log().error("Function " + function.name()
            + " has faulty native implementation: Its declared result spec == " + spec.name()
            + " but it returned record with spec == " + result.spec().name() + ".");
        return new Output(null, nativeApi.messages());
      }
      return new Output(result, nativeApi.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof AbortException) {
        return nullOutput(nativeApi);
      } else {
        throw new NativeCallException(
            "Function " + function.name() + " threw java exception from its native code.", cause);
      }
    }
  }

  private Output nullOutput(NativeApi nativeApi) {
    if (!containsErrors(nativeApi.messages())) {
      nativeApi.log().error("Function " + function.name()
          + " has faulty native implementation: it returned 'null' but logged no error.");
    }
    return new Output(null, nativeApi.messages());
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
