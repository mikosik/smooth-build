package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.type.val.MethodTH;

/**
 * Native method. Encapsulates java jar and thus java method to invoke.
 *
 * This class is thread-safe.
 */
public final class MethodH extends ValH {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int JAR_INDEX = 0;
  private static final int CLASS_BINARY_NAME_INDEX = 1;
  private static final int IS_PURE_INDEX = 2;

  public MethodH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public MethodTH cat() {
    return (MethodTH) super.cat();
  }

  @Override
  public MethodTH type() {
    return cat();
  }

  public BlobH jar() {
    return readSeqElemObj(DATA_PATH, dataHash(), JAR_INDEX, DATA_SEQ_SIZE, BlobH.class);
  }

  public StringH classBinaryName() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), CLASS_BINARY_NAME_INDEX, DATA_SEQ_SIZE, StringH.class);
  }

  public BoolH isPure() {
    return readSeqElemObj(DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQ_SIZE, BoolH.class);
  }

  @Override
  public String objToString() {
    return "Method(" + cat().name() + ")";
  }
}
