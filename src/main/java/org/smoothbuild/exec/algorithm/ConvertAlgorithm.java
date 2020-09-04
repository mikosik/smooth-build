package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class ConvertAlgorithm implements Algorithm {
  private final Spec destinationSpec;

  public ConvertAlgorithm(Spec destinationSpec) {
    this.destinationSpec = destinationSpec;
  }

  @Override
  public Hash hash() {
    return convertAlgorithmHash(destinationSpec);
  }

  @Override
  public Spec outputSpec() {
    return destinationSpec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    assertThat(input.objects().size() == 1);
    Obj object = input.objects().get(0);
    assertThat(!destinationSpec.equals(object.spec()));
    if (object instanceof Array array) {
      return new Output(convertArray(nativeApi, array, destinationSpec), nativeApi.messages());
    }
    assertThat(!object.spec().isNothing());
    return new Output(convertStruct((Tuple) object, destinationSpec), nativeApi.messages());
  }

  private static Obj convertArray(NativeApi nativeApi, Array array, Spec destinationSpec) {
    Spec elemSpec = ((ArraySpec) destinationSpec).elemSpec();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(elemSpec);
    for (Obj element : array.asIterable(Obj.class)) {
      if (element instanceof Array arr) {
        builder.add(convertArray(nativeApi, arr, elemSpec));
      } else {
        builder.add(convertStruct((Tuple) element, elemSpec));
      }
    }
    return builder.build();
  }

  private static Obj convertStruct(Tuple tuple, Spec destinationSpec) {
    Obj superObject = tuple.superObject();
    if (superObject.spec().equals(destinationSpec)) {
      return superObject;
    }
    return convertStruct((Tuple) superObject, destinationSpec);
  }

  private static void assertThat(boolean expression) {
    if (!expression) {
      throw new RuntimeException(
          "This should not happen. It means smooth build release is broken.");
    }
  }
}
