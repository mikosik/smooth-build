package org.smoothbuild.exec.algorithm;

import static java.util.Arrays.asList;

import java.math.BigInteger;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.db.object.type.val.TupleTypeO;

import okio.ByteString;

public class AlgorithmHashes {
  public static Hash orderAlgorithmHash() {
    return hash(0);
  }

  public static Hash callNativeAlgorithmHash(String referencableName) {
    return hash(1, Hash.of(referencableName));
  }

  public static Hash convertAlgorithmHash(TypeO destinationType) {
    return hash(2, destinationType.hash());
  }

  public static Hash constructAlgorithmHash(TupleTypeO type) {
    return hash(3, type.hash());
  }

  public static Hash selectAlgorithmHash(int itemIndex) {
    return hash(4, Hash.of(itemIndex));
  }

  public static Hash fixedStringAlgorithmHash(String string) {
    return hash(5, Hash.of(string));
  }

  public static Hash fixedBlobAlgorithmHash(ByteString byteString) {
    return hash(6, Hash.of(byteString));
  }

  public static Hash referenceAlgorithmHash(Hash moduleHash, String functionName) {
    return hash(7, Hash.of(asList(moduleHash, Hash.of(functionName))));
  }

  public static Hash fixedIntAlgorithmHash(BigInteger bigInteger) {
    return hash(8, Hash.of(ByteString.of(bigInteger.toByteArray())));
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
