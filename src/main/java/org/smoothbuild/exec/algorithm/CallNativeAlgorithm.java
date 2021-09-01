package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.base.MessageRec.containsErrors;
import static org.smoothbuild.util.Lists.skip;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class CallNativeAlgorithm extends Algorithm {
  private final MethodLoader methodLoader;
  private final GlobalReferencable referencable;

  public CallNativeAlgorithm(MethodLoader methodLoader, ValSpec outputSpec,
      GlobalReferencable referencable, boolean isPure) {
    super(outputSpec, isPure);
    this.methodLoader = methodLoader;
    this.referencable = referencable;
  }

  @Override
  public Hash hash() {
    return callNativeAlgorithmHash(referencable.name());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    String methodPath = ((Str) input.vals().get(0)).jValue();
    Method method = methodLoader.load(referencable, methodPath);
    try {
      ImmutableList<Val> nativeArgs = skip(1, input.vals());
      Val result = (Val) method.invoke(null, createArguments(nativeApi, nativeArgs));
      if (result == null) {
        if (!containsErrors(nativeApi.messages())) {
          nativeApi.log().error("`" + referencable.name()
              + "` has faulty native implementation: it returned `null` but logged no error.");
        }
        return new Output(null, nativeApi.messages());
      }
      if (!outputSpec().equals(result.spec())) {
        nativeApi.log().error("`" + referencable.name()
            + "` has faulty native implementation: Its declared result spec == "
            + outputSpec().name()
            + " but it returned object with spec == " + result.spec().name() + ".");
        return new Output(null, nativeApi.messages());
      }
      return new Output(result, nativeApi.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new NativeCallException("`" + referencable.name()
          + "` threw java exception from its native code.", e.getCause());
    }
  }

  private static Object[] createArguments(NativeApi nativeApi, List<Val> arguments) {
    Object[] nativeArguments = new Object[1 + arguments.size()];
    nativeArguments[0] = nativeApi;
    for (int i = 0; i < arguments.size(); i++) {
      nativeArguments[i + 1] = arguments.get(i);
    }
    return nativeArguments;
  }
}
