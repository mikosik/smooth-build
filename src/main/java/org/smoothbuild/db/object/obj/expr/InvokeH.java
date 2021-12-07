package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.type.expr.InvokeCH;

/**
 * Native call expression. It invokes java method from jar.
 *
 * This class is thread-safe.
 */
public final class InvokeH extends ExprH {
  private static final int DATA_SEQ_SIZE = 4;
  private static final int JAR_FILE_INDEX = 0;
  private static final int CLASS_BINARY_NAME_INDEX = 1;
  private static final int IS_PURE_INDEX = 2;
  private static final int ARGS_INDEX = 3;

  public InvokeH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public InvokeCH cat() {
    return (InvokeCH) super.cat();
  }

  public BlobH jarFile() {
    return readSeqElemObj(DATA_PATH, dataHash(), JAR_FILE_INDEX, DATA_SEQ_SIZE, BlobH.class);
  }

  public StringH classBinaryName() {
    return readSeqElemObj(
        DATA_PATH, dataHash(), CLASS_BINARY_NAME_INDEX, DATA_SEQ_SIZE, StringH.class);
  }

  public BoolH isPure() {
    return readSeqElemObj(DATA_PATH, dataHash(), IS_PURE_INDEX, DATA_SEQ_SIZE, BoolH.class);
  }

  public CombineH args() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARGS_INDEX, DATA_SEQ_SIZE, CombineH.class);
  }
}
