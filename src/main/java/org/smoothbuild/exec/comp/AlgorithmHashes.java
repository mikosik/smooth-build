package org.smoothbuild.exec.comp;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.StructType;

public class AlgorithmHashes {
  public static Hash arrayAlgorithmHash() {
    return hash(0);
  }

  public static Hash nativeCallAlgorithmHash(NativeFunction function) {
    return hash(1, function.hash());
  }

  public static Hash convertAlgorithmHash(ConcreteType destinationType) {
    return hash(2, destinationType.hash());
  }

  public static Hash constructorCallAlgorithmHash(Constructor constructor) {
    StructType type = constructor.type();
    return hash(3, type.hash());
  }

  public static Hash accessorCallAlgorithmHash(Accessor accessor) {
    return hash(4, Hash.of(accessor.fieldName()));
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(Hash.of(id), hash);
  }

  private static Hash hash(int id) {
    return Hash.of(Hash.of(id));
  }
}
