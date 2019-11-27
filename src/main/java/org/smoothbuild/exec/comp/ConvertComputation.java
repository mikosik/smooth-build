package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.ComputationHashes.convertComputationHash;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.Container;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.ArrayBuilder;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;

public class ConvertComputation implements Computation {
  private final ConcreteType type;

  public ConvertComputation(ConcreteType type) {
    this.type = type;
  }

  @Override
  public Hash hash() {
    return convertComputationHash(type);
  }

  @Override
  public ConcreteType type() {
    return type;
  }

  @Override
  public Output execute(Input input, Container container) {
    assertThat(input.objects().size() == 1);
    SObject object = input.objects().get(0);
    assertThat(!type.equals(object.type()));
    assertThat(type.isAssignableFrom(object.type()));
    if (object instanceof Array) {
      return new Output(convertArray(container, (Array) object, type), emptyMessageArray(container));
    }
    assertThat(!object.type().isNothing());
    return new Output(convertStruct((Struct) object, type), emptyMessageArray(container));
  }

  private static SObject convertArray(Container container, Array array,
      ConcreteType destinationType) {
    ConcreteType elemType = ((ConcreteArrayType) destinationType).elemType();
    ArrayBuilder builder = container.factory().arrayBuilder(elemType);
    for (SObject element : array.asIterable(SObject.class)) {
      if (element instanceof Array) {
        builder.add(convertArray(container, (Array) element, elemType));
      } else {
        builder.add(convertStruct((Struct) element, elemType));
      }
    }
    return builder.build();
  }

  private static SObject convertStruct(Struct struct,
      ConcreteType destinationType) {
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
