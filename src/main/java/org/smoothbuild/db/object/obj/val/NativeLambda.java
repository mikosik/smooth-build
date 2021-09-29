package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.RecExpr;

public class NativeLambda extends Lambda {
  private static final int NATIVE_BODY_SEQUENCE_SIZE = 2;

  private static final int CLASS_BINARY_NAME_INDEX = 0;
  private static final int NATIVE_JAR_INDEX = 1;

  public NativeLambda(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public String valueToString() {
    return "NativeLambda(" + spec().name() + ")";
  }


  public NativeLambdaData data() {
    Hash bodyHash = readSequenceElementHash(DATA_PATH, dataHash(), BODY_INDEX, DATA_SEQUENCE_SIZE);
    Blob nativeJar = readSequenceElementObj(
        BODY_PATH, bodyHash, NATIVE_JAR_INDEX, NATIVE_BODY_SEQUENCE_SIZE, Blob.class);
    Str classBinaryName = readSequenceElementObj(
        BODY_PATH, bodyHash, CLASS_BINARY_NAME_INDEX, NATIVE_BODY_SEQUENCE_SIZE, Str.class);
    return new NativeLambdaData(classBinaryName, nativeJar, defaultArguments());
  }

  public record NativeLambdaData(
      Str classBinaryName, Blob nativeJar, RecExpr defaultArguments) {}
}
