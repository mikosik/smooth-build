package org.smoothbuild.exec.algorithm;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.db.record.spec.TupleSpec;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Native;

import okio.ByteString;

public class AlgorithmHashes {
  public static Hash arrayAlgorithmHash() {
    return hash(0);
  }

  public static Hash callNativeAlgorithmHash(Native nativ) {
    return hash(1, nativ.hash());
  }

  public static Hash convertAlgorithmHash(Spec destinationSpec) {
    return hash(2, destinationSpec.hash());
  }

  public static Hash tupleAlgorithmHash(TupleSpec type) {
    return hash(3, type.hash());
  }

  public static Hash readTupleElementAlgorithmHash(Accessor accessor) {
    return hash(4, Hash.of(accessor.fieldIndex()));
  }

  public static Hash fixedStringAlgorithmHash(String string) {
    return hash(5, Hash.of(string));
  }

  public static Hash fixedBlobAlgorithmHash(ByteString byteString) {
    return hash(6, Hash.of(byteString));
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(Hash.of(id), hash);
  }

  private static Hash hash(int id) {
    return Hash.of(Hash.of(id));
  }
}
