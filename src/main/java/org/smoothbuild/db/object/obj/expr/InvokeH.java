package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.MethodH;
import org.smoothbuild.db.object.type.expr.InvokeCH;

/**
 * Native call expression. It invokes java method represented by MethodH.
 *
 * This class is thread-safe.
 */
public final class InvokeH extends ExprH {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int METHOD_INDEX = 0;
  private static final int ARGS_INDEX = 1;

  public InvokeH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public InvokeCH cat() {
    return (InvokeCH) super.cat();
  }

  public record Data(MethodH method, CombineH args) {}

  public Data data() {
    return new Data(method(), args());
  }

  private MethodH method() {
    return readSeqElemObj(DATA_PATH, dataHash(), METHOD_INDEX, DATA_SEQ_SIZE, MethodH.class);
  }

  private CombineH args() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARGS_INDEX, DATA_SEQ_SIZE, CombineH.class);
  }
}
