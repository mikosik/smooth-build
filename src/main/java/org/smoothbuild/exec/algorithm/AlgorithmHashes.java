package org.smoothbuild.exec.algorithm;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.io.fs.space.FilePath;

import okio.ByteString;

public class AlgorithmHashes {
  public static Hash arrayAlgorithmHash() {
    return hash(0);
  }

  public static Hash callNativeAlgorithmHash(String referencableName) {
    return hash(1, Hash.of(referencableName));
  }

  public static Hash convertAlgorithmHash(Spec destinationSpec) {
    return hash(2, destinationSpec.hash());
  }

  public static Hash tupleAlgorithmHash(Spec type) {
    return hash(3, type.hash());
  }

  public static Hash readTupleElementAlgorithmHash(int elementIndex) {
    return hash(4, Hash.of(elementIndex));
  }

  public static Hash fixedStringAlgorithmHash(String string) {
    return hash(5, Hash.of(string));
  }

  public static Hash fixedBlobAlgorithmHash(ByteString byteString) {
    return hash(6, Hash.of(byteString));
  }

  public static Hash readFileContentAlgorithmHash(FilePath filePath) {
    return hash(7, filePath.hash());
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(Hash.of(id), hash);
  }

  private static Hash hash(int id) {
    return Hash.of(Hash.of(id));
  }
}
