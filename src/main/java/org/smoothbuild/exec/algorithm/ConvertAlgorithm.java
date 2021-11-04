package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.val.ArrayOType;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class ConvertAlgorithm extends Algorithm {
  public ConvertAlgorithm(TypeV outputType) {
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
    Obj obj = input.vals().get(0);
    assertThatTypesAreNotEqual(obj);
    return new Output(convert(outputType(), obj, nativeApi), nativeApi.messages());
  }

  private void assertThatTypesAreNotEqual(Obj obj) {
    if (outputType().equals(obj.type())) {
      throw newBuildBrokenException(
          "Expected non equal types but got " + outputType() + " " + obj.type());
    }
  }

  private static Val convert(TypeO destinationType, Obj obj, NativeApi nativeApi) {
    if (obj instanceof Array array) {
      return convertArray(destinationType, array, nativeApi);
    }
    throw newBuildBrokenException("Expected `Array` type but got " + obj.getClass());
  }

  private static Array convertArray(TypeO destinationType, Array array, NativeApi nativeApi) {
    TypeV elementType = ((ArrayOType) destinationType).element();
    ArrayBuilder arrayBuilder = nativeApi.factory().arrayBuilder(elementType);
    for (Val element : array.elements(Val.class)) {
      arrayBuilder.add(convert(elementType, element, nativeApi));
    }
    return arrayBuilder.build();
  }

  private static RuntimeException newBuildBrokenException(String message) {
    return new RuntimeException(
        "This should not happen. It means smooth build release is broken." + message);
  }
}
