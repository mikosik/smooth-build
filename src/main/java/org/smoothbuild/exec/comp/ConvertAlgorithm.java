package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.convertAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.spec.ArraySpec;
import org.smoothbuild.record.spec.Spec;

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
  public Spec type() {
    return destinationSpec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    assertThat(input.records().size() == 1);
    Record record = input.records().get(0);
    assertThat(!destinationSpec.equals(record.spec()));
    if (record instanceof Array array) {
      return new Output(convertArray(nativeApi, array, destinationSpec), nativeApi.messages());
    }
    assertThat(!record.spec().isNothing());
    return new Output(convertStruct((Tuple) record, destinationSpec), nativeApi.messages());
  }

  private static Record convertArray(NativeApi nativeApi, Array array, Spec destinationSpec) {
    Spec elemSpec = ((ArraySpec) destinationSpec).elemSpec();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(elemSpec);
    for (Record element : array.asIterable(Record.class)) {
      if (element instanceof Array arr) {
        builder.add(convertArray(nativeApi, arr, elemSpec));
      } else {
        builder.add(convertStruct((Tuple) element, elemSpec));
      }
    }
    return builder.build();
  }

  private static Record convertStruct(Tuple tuple, Spec destinationSpec) {
    Record superObject = tuple.superObject();
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
