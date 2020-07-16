package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.convertAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.CONVERSION;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.BinaryType;
import org.smoothbuild.lang.plugin.NativeApi;

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
    return new Output(convertStruct((Struct) object, destinationType), nativeApi.messages());
  }

  private static SObject convertArray(NativeApi nativeApi, Array array,
      BinaryType destinationType) {
    BinaryType elemType = ((ArrayType) destinationType).elemType();
    ArrayBuilder builder = nativeApi.factory().arrayBuilder(elemType);
    for (SObject element : array.asIterable(SObject.class)) {
      if (element instanceof Array) {
        builder.add(convertArray(nativeApi, (Array) element, elemType));
      } else {
        builder.add(convertStruct((Struct) element, elemType));
      }
    }
    return builder.build();
  }

  private static SObject convertStruct(Struct struct,
      BinaryType destinationType) {
    SObject superObject = struct.superObject();
    if (superObject.type().equals(destinationType)) {
      return superObject;
    }
    return convertStruct((Struct) superObject, destinationType);
  }

  private static void assertThat(boolean expression) {
    if (!expression) {
      throw new RuntimeException(
          "This should not happen. It means smooth build release is broken.");
    }
  }
}
