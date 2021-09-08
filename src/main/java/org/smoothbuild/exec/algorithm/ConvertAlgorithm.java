package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class ConvertAlgorithm extends Algorithm {
  public ConvertAlgorithm(ValSpec outputSpec) {
    super(outputSpec);
  }

  @Override
  public Hash hash() {
    return convertAlgorithmHash(outputSpec());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    if (input.vals().size() != 1) {
      throw newBuildBrokenException("Expected input size == 1 but was " + input.vals().size());
    }
    Obj obj = input.vals().get(0);
    assertThatSpecsAreNotEqual(obj);
    return new Output(convert(outputSpec(), obj, nativeApi), nativeApi.messages());
  }

  private void assertThatSpecsAreNotEqual(Obj obj) {
    if (outputSpec().equals(obj.spec())) {
      throw newBuildBrokenException(
          "Expected non equal specs but got " + outputSpec() + " " + obj.spec());
    }
  }

  private static Val convert(Spec destinationSpec, Obj obj, NativeApi nativeApi) {
    if (obj instanceof Array array) {
      return convertArray(destinationSpec, array, nativeApi);
    }
    throw newBuildBrokenException("Expected `Array` spec but got " + obj.getClass());
  }

  private static Array convertArray(Spec destinationSpec, Array array, NativeApi nativeApi) {
    ValSpec elementSpec = ((ArraySpec) destinationSpec).element();
    ArrayBuilder arrayBuilder = nativeApi.factory().arrayBuilder(elementSpec);
    for (Val element : array.elements(Val.class)) {
      arrayBuilder.add(convert(elementSpec, element, nativeApi));
    }
    return arrayBuilder.build();
  }

  private static RuntimeException newBuildBrokenException(String message) {
    return new RuntimeException(
        "This should not happen. It means smooth build release is broken." + message);
  }
}
