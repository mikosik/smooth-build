package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.MethodH;
import org.smoothbuild.db.object.type.expr.InvokeCH;

/**
 * Native call expression. It invokes java method represented by MethodH.
 *
 * This class is thread-safe.
 */
public final class InvokeH extends CallLikeH {
  public InvokeH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.cat() instanceof InvokeCH);
  }

  @Override
  public InvokeCH cat() {
    return (InvokeCH) super.cat();
  }

  public record Data(MethodH method, CombineH args) {}

  public Data data() {
    var method = method();
    var args = args();
    validate(method.type(), args);
    return new Data(method, args);
  }

  private MethodH method() {
    return readSeqElemObj(DATA_PATH, dataHash(), CALLABLE_INDEX, DATA_SEQ_SIZE, MethodH.class);
  }

  private CombineH args() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARGS_INDEX, DATA_SEQ_SIZE, CombineH.class);
  }
}
