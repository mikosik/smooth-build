package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.convertAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.CONVERSION;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.ArrayBuilder;
import org.smoothbuild.record.base.SObject;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.type.ArrayType;
import org.smoothbuild.record.type.BinaryType;

public class ConvertAlgorithm implements Algorithm {
  private final BinaryType destinationType;

  public ConvertAlgorithm(BinaryType destinationType) {
    this.destinationType = destinationType;
  }

  @Override
  public TaskKind kind() {
    return CONVERSION;
  }

  @Override
  public Hash hash() {
    return convertAlgorithmHash(destinationType);
  }

  @Override
  public BinaryType type() {
    return destinationType;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    assertThat(input.objects().size() == 1);
    SObject object = input.objects().get(0);
    assertThat(!destinationType.equals(object.type()));
    if (object instanceof Array) {
      return new Output(convertArray(nativeApi, (Array) object, destinationType), nativeApi.messages());
    }
    assertThat(!object.type().isNothing());
    return new Output(convertStruct((Tuple) object, destinationType), nativeApi.messages());
  }

  private static SObject convertArray(NativeApi nativeApi, Array array,
      BinaryType destinationType) {
    BinaryType elemType = ((ArrayType) destinationType).elemType();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(elemType);
    for (SObject element : array.asIterable(SObject.class)) {
      if (element instanceof Array) {
        builder.add(convertArray(nativeApi, (Array) element, elemType));
      } else {
        builder.add(convertStruct((Tuple) element, elemType));
      }
    }
    return builder.build();
  }

  private static SObject convertStruct(Tuple tuple,
      BinaryType destinationType) {
    SObject superObject = tuple.superObject();
    if (superObject.type().equals(destinationType)) {
      return superObject;
    }
    return convertStruct((Tuple) superObject, destinationType);
  }

  private static void assertThat(boolean expression) {
    if (!expression) {
      throw new RuntimeException(
          "This should not happen. It means smooth build release is broken.");
    }
  }
}
