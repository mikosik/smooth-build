package org.smoothbuild.exec.comp;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.object.type.BinaryType;
import org.smoothbuild.lang.object.type.TupleType;

public class AlgorithmHashes {
  public static Hash createArrayAlgorithmHash() {
    return hash(0);
  }

  public static Hash callNativeAlgorithmHash(NativeFunction function) {
    return hash(1, function.hash());
  }

  public static Hash convertAlgorithmHash(BinaryType destinationType) {
    return hash(2, destinationType.hash());
  }

  public static Hash createTupleAlgorithmHash(TupleType type) {
    return hash(3, type.hash());
  }

  public static Hash ReadTupleElementAlgorithmHash(Accessor accessor) {
    return hash(4, Hash.of(accessor.fieldIndex()));
  }

  public static Hash fixedStringAlgorithmHash(String string) {
    return hash(5, Hash.of(string));
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(Hash.of(id), hash);
  }

  private static Hash hash(int id) {
    return Hash.of(Hash.of(id));
  }
}
