package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Val;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.ValSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class ConvertAlgorithm extends Algorithm {
  public ConvertAlgorithm(Spec outputSpec) {
    super(outputSpec);
  }

  @Override
  public Hash hash() {
    return convertAlgorithmHash(outputSpec());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    if (input.objects().size() != 1) {
      throw newBuildBrokenException("Expected input size == 1 but was " + input.objects().size());
    }
    Obj obj = input.objects().get(0);
    assertThatSpecsAreNotEqual(obj);
    return new Output(convert(outputSpec(), obj, nativeApi), nativeApi.messages());
  }

  private void assertThatSpecsAreNotEqual(Obj obj) {
    if (outputSpec().equals(obj.spec())) {
      throw newBuildBrokenException(
          "Expected non equal specs but got " + outputSpec() + " " + obj.spec());
    }
  }

  private static Obj convert(Spec destinationSpec, Obj obj, NativeApi nativeApi) {
    if (obj instanceof Array array) {
      return convertArray(destinationSpec, array, nativeApi);
    }
    throw newBuildBrokenException("Expected `Array` spec but got " + obj.getClass());
  }

  private static Array convertArray(Spec destinationSpec, Array array, NativeApi nativeApi) {
    ValSpec elemSpec = ((ArraySpec) destinationSpec).elemSpec();
    ArrayBuilder arrayBuilder = nativeApi.factory().arrayBuilder(elemSpec);
    for (Val element : array.asIterable(Val.class)) {
      arrayBuilder.add(convert(elemSpec, element, nativeApi));
    }
    return arrayBuilder.build();
  }

  private static RuntimeException newBuildBrokenException(String message) {
    return new RuntimeException(
        "This should not happen. It means smooth build release is broken." + message);
  }
}
