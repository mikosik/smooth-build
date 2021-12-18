package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.ExprB;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjB;

/**
 * Map expression.
 *
 * This class is thread-safe.
 */
public final class MapB extends ExprB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int ARRAY_INDEX = 0;
  private static final int FUNC_INDEX = 1;

  public MapB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
  }

  public Data data() {
    return new Data(readArray(), readFunc());
  }

  public record Data(ObjB array, ObjB func) {}

  private ObjB readArray() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARRAY_INDEX, DATA_SEQ_SIZE, ObjB.class);
  }

  private ObjB readFunc() {
    return readSeqElemObj(DATA_PATH, dataHash(), FUNC_INDEX, DATA_SEQ_SIZE, ObjB.class);
  }
}
