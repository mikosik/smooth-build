package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.type.base.CatKindH.NAT_FUNC;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;

/**
 * Native function.
 * This class is thread-safe.
 */
public final class NatFuncH extends FuncH {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int JAR_FILE_INDEX = 0;
  private static final int CLASS_BINARY_NAME_INDEX = 1;
  private static final int IS_PURE_INDEX = 2;

  public NatFuncH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb, NAT_FUNC);
  }

  public BlobH jarFile() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), JAR_FILE_INDEX, DATA_SEQ_SIZE, BlobH.class);
  }

  public StringH classBinaryName() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), CLASS_BINARY_NAME_INDEX, DATA_SEQ_SIZE, StringH.class);
  }

  public BoolH isPure() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQ_SIZE, BoolH.class);
  }

  @Override
  public String objToString() {
    return "NatFuncH(???)";
  }
}
