package org.smoothbuild.task.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.value.Value;

public class ComputationHashes {
  public static Hash valueComputationHash(Value value) {
    return hash(0, value.hash());
  }

  public static Hash arrayComputationHash() {
    return hash(1);
  }

  public static Hash identityComputationHash() {
    return hash(2);
  }

  public static Hash nativeCallComputationHash(NativeFunction function) {
    return hash(3, function.hash());
  }

  public static Hash convertComputationHash(ConcreteType destinationType) {
    return hash(4, destinationType.hash());
  }

  public static Hash constructorCallComputationHash(Constructor constructor) {
    StructType type = constructor.type();
    return hash(5, type.hash());
  }

  public static Hash accessorCallComputationHash(Accessor accessor) {
    return hash(6, Hash.of(accessor.fieldName()));
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(Hash.of(id), hash);
  }

  private static Hash hash(int id) {
    return Hash.of(Hash.of(id));
  }
}
