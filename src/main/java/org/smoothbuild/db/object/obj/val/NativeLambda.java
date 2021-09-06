package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

public class NativeLambda extends Lambda {
  private static final int NATIVE_BODY_SEQUENCE_SIZE = 2;

  private static final int CLASS_BINARY_NAME_INDEX = 0;
  private static final int NATIVE_JAR_INDEX = 1;

  public NativeLambda(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public String valueToString() {
    return "NativeLambda(???)";
  }

  public Str classBinaryName() {
    Hash bodyHash = bodyHash();
    return readSequenceElementObj(
        BODY_PATH, bodyHash, CLASS_BINARY_NAME_INDEX, NATIVE_BODY_SEQUENCE_SIZE, Str.class);
  }

  public Blob nativeJar() {
    Hash bodyHash = bodyHash();
    return readSequenceElementObj(
        BODY_PATH, bodyHash, NATIVE_JAR_INDEX, NATIVE_BODY_SEQUENCE_SIZE, Blob.class);
  }

  protected Hash bodyHash() {
    return readSequenceElementHash(DATA_PATH, dataHash(), BODY_INDEX, DATA_SEQUENCE_SIZE);
  }
}
