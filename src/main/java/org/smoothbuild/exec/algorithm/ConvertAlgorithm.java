package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.spec.AnySpec;
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
    Obj obj = input.objects().get(0);
    assertThat(!destinationSpec.equals(obj.spec()));
    return new Output(convert(destinationSpec, obj, nativeApi), nativeApi.messages());
  }

  private static Obj convert(Spec destinationSpec, Obj obj, NativeApi nativeApi) {
    if (destinationSpec instanceof AnySpec) {
      return nativeApi.factory().any(obj.hash());
    } else if (obj instanceof Array array) {
      return convertArray(destinationSpec, array, nativeApi);
    }
    throw newBuildBrokenException();
  }

  private static Obj convertArray(Spec destinationSpec, Array array, NativeApi nativeApi) {
    Spec elemSpec = ((ArraySpec) destinationSpec).elemSpec();
    ArrayBuilder arrayBuilder = nativeApi.factory().arrayBuilder(elemSpec);
    for (Obj element : array.asIterable(Obj.class)) {
      arrayBuilder.add(convert(elemSpec, element, nativeApi));
    }
    return arrayBuilder.build();
  }

  private static void assertThat(boolean expression) {
    if (!expression) {
      throw newBuildBrokenException();
    }
  }

  private static RuntimeException newBuildBrokenException() {
    return new RuntimeException("This should not happen. It means smooth build release is broken.");
  }
}
