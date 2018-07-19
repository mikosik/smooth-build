package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.ComputationHashes.convertComputationHash;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

public class ConvertComputation implements Computation {
  private final Type type;

  public ConvertComputation(Type type) {
    this.type = type;
  }

  @Override
  public HashCode hash() {
    return convertComputationHash(type);
  }

  @Override
  public Type resultType() {
    return type;
  }

  @Override
  public Output execute(Input input, Container container) {
    assertThat(input.values().size() == 1);
    Value value = input.values().get(0);
    assertThat(type.isAssignableFrom(value.type()));
    if (value instanceof Array) {
      return new Output(convertArray(container, (Array) value, type));
    }
    assertThat(!value.type().isNothing());
    return new Output(convertStruct(container, (Struct) value, type));
  }

  private static Value convertArray(Container container, Array array, Type destinationType) {
    Type elemType = ((ArrayType) destinationType).elemType();
    ArrayBuilder builder = container.create().arrayBuilder(elemType);
    for (Value element : array.asIterable(Value.class)) {
      if (element instanceof Array) {
        builder.add(convertArray(container, (Array) element, elemType));
      } else {
        builder.add(convertStruct(container, (Struct) element, elemType));
      }
    }
    return builder.build();
  }

  private static Value convertStruct(Container container, Struct struct, Type destinationType) {
    Value superValue = struct.superValue();
    if (superValue.type().equals(destinationType)) {
      return superValue;
    }
    return convertStruct(container, (Struct) superValue, destinationType);
  }

  private static void assertThat(boolean expression) {
    if (!expression) {
      throw new RuntimeException(
          "This should not happen. It means smooth build release is broken.");
    }
  }
}
