package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.type.val.MethodTB;

/**
 * Native method. Encapsulates java jar and thus java method to invoke.
 *
 * This class is thread-safe.
 */
public final class MethodB extends ValB {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int JAR_INDEX = 0;
  private static final int CLASS_BINARY_NAME_INDEX = 1;
  private static final int IS_PURE_INDEX = 2;

  public MethodB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
  }

  @Override
  public MethodTB cat() {
    return (MethodTB) super.cat();
  }

  @Override
  public MethodTB type() {
    return cat();
  }

  public BlobB jar() {
    return readSeqElemObj(DATA_PATH, dataHash(), JAR_INDEX, DATA_SEQ_SIZE, BlobB.class);
  }

  public StringB classBinaryName() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), CLASS_BINARY_NAME_INDEX, DATA_SEQ_SIZE, StringB.class);
  }

  public BoolB isPure() {
    return readSeqElemObj(DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQ_SIZE, BoolB.class);
  }

  @Override
  public String objToString() {
    return "Method(" + cat().name() + ")";
  }
}
