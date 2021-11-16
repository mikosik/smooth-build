package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.base.MessageStruct.containsErrors;
import static org.smoothbuild.util.collect.Lists.skip;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.java.MethodLoader;
import org.smoothbuild.lang.base.define.TopEvaluableS;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class CallNativeAlgorithm extends Algorithm {
  private final MethodLoader methodLoader;
  private final TopEvaluableS evaluable;

  public CallNativeAlgorithm(MethodLoader methodLoader, TypeHV outputType, TopEvaluableS evaluable,
      boolean isPure) {
    super(outputType, isPure);
    this.methodLoader = methodLoader;
    this.evaluable = evaluable;
  }

  @Override
  public Hash hash() {
    return callNativeAlgorithmHash(evaluable.name());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    String classBinaryName = ((StringH) input.vals().get(0)).jValue();
    Method method = methodLoader.load(evaluable, classBinaryName);
    try {
      ImmutableList<ValueH> nativeArgs = skip(1, input.vals());
      ValueH result = (ValueH) method.invoke(null, createArguments(nativeApi, nativeArgs));
      if (result == null) {
        if (!containsErrors(nativeApi.messages())) {
          nativeApi.log().error("`" + evaluable.name()
              + "` has faulty native implementation: it returned `null` but logged no error.");
        }
        return new Output(null, nativeApi.messages());
      }
      if (!outputType().equals(result.type())) {
        nativeApi.log().error("`" + evaluable.name()
            + "` has faulty native implementation: Its declared result type == "
            + outputType().q()
            + " but it returned object with type == " + result.type().q() + ".");
        return new Output(null, nativeApi.messages());
      }
      return new Output(result, nativeApi.messages());
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new NativeCallException("`" + evaluable.name()
          + "` threw java exception from its native code.", e.getCause());
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
