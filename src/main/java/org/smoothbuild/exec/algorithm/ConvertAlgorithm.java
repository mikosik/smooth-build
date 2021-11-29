package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class ConvertAlgorithm extends Algorithm {
  public ConvertAlgorithm(TypeH outputType) {
    super(outputType);
  }

  @Override
  public Hash hash() {
    return convertAlgorithmHash(outputType());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    if (input.vals().size() != 1) {
      throw newBuildBrokenException("Expected input size == 1 but was " + input.vals().size());
    }
    ObjectH obj = input.vals().get(0);
    assertThatTypesAreNotEqual(obj);
    return new Output(convert(outputType(), obj, nativeApi), nativeApi.messages());
  }

  private void assertThatTypesAreNotEqual(ObjectH obj) {
    if (outputType().equals(obj.spec())) {
      throw newBuildBrokenException(
          "Expected non equal types but got " + outputType() + " " + obj.spec());
    }
  }

  private static ValueH convert(SpecH destinationType, ObjectH obj, NativeApi nativeApi) {
    if (obj instanceof ArrayH array) {
      return convertArray(destinationType, array, nativeApi);
    }
    throw newBuildBrokenException("Expected `Array` type but got " + obj.getClass());
  }

  private static ArrayH convertArray(SpecH destinationType, ArrayH array, NativeApi nativeApi) {
    TypeH elemType = ((ArrayTypeH) destinationType).elem();
    ArrayHBuilder arrayBuilder = nativeApi.factory().arrayBuilder(elemType);
    for (ValueH elem : array.elems(ValueH.class)) {
      arrayBuilder.add(convert(elemType, elem, nativeApi));
    }
    return arrayBuilder.build();
  }

  private static RuntimeException newBuildBrokenException(String message) {
    return new RuntimeException(
        "This should not happen. It means smooth build release is broken. " + message);
  }
}
