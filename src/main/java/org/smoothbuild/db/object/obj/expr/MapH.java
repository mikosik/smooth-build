package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;

/**
 * Map expression.
 *
 * This class is thread-safe.
 */
public final class MapH extends ExprH {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int ARRAY_INDEX = 0;
  private static final int FUNC_INDEX = 1;

  public MapH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public Data data() {
    return new Data(readArray(), readFunc());
  }

  public record Data(ObjH array, ObjH func) {}

  private ObjH readArray() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARRAY_INDEX, DATA_SEQ_SIZE, ObjH.class);
  }

  private ObjH readFunc() {
    return readSeqElemObj(DATA_PATH, dataHash(), FUNC_INDEX, DATA_SEQ_SIZE, ObjH.class);
  }
}
