package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.base.MessageTuple.containsErrors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.NativeCodeTuple;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.nativ.LoadingNativeImplException;
import org.smoothbuild.exec.nativ.Native;
import org.smoothbuild.exec.nativ.NativeImplLoader;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class CallNativeAlgorithm extends Algorithm {
  private final NativeImplLoader nativeImplLoader;
  private final Referencable referencable;

  public CallNativeAlgorithm(NativeImplLoader nativeImplLoader, Spec outputSpec,
      Referencable referencable, boolean isPure) {
    super(outputSpec, isPure);
    this.nativeImplLoader = nativeImplLoader;
    this.referencable = referencable;
  }

  @Override
  public Hash hash() {
    return callNativeAlgorithmHash(referencable.name());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws Exception {
    Native nativ = loadNative((Tuple) input.objects().get(0));
    try {
      ImmutableList<Obj> nativeArgs = skipFirstArgument(input.objects());
      Obj result = (Obj) nativ.method()
          .invoke(null, createArguments(nativeApi, nativeArgs));
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

  private static ImmutableList<Obj> skipFirstArgument(ImmutableList<Obj> objs) {
    return objs.subList(1, objs.size());
  }

  private Native loadNative(Tuple nativeCode) throws LoadingNativeImplException {
    Blob content = NativeCodeTuple.content(nativeCode);
    String methodPath = NativeCodeTuple.methodPath(nativeCode).jValue();
    if (referencable instanceof Function function) {
      return nativeImplLoader.loadNative(function, methodPath, content.hash());
    } else {
      return nativeImplLoader.loadNative((Value) referencable, methodPath, content.hash());
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
