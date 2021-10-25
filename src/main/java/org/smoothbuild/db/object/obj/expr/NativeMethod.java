package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.expr.NativeMethodSpec;

/**
 * This class is immutable.
 */
public class NativeMethod extends Val {
  private static final int DATA_SEQUENCE_SIZE = 2;
  private static final int JAR_FILE_INDEX = 0;
  private static final int CLASS_BINARY_NAME_INDEX = 1;

  public NativeMethod(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof NativeMethodSpec);
  }

  public Blob jarFile() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), JAR_FILE_INDEX, DATA_SEQUENCE_SIZE, Blob.class);
  }

  public Str classBinaryName() {
    return readSequenceElementObj(
        DATA_PATH, dataHash(), CLASS_BINARY_NAME_INDEX, DATA_SEQUENCE_SIZE, Str.class);
  }

  @Override
  public String valueToString() {
    return "NativeMethod(???)";
  }
}
