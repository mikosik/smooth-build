package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.MethodB;
import org.smoothbuild.db.object.type.expr.InvokeCB;

/**
 * Native call expression. It invokes java method represented by MethodH.
 *
 * This class is thread-safe.
 */
public final class InvokeB extends CallLikeB {
  public InvokeB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
    checkArgument(merkleRoot.cat() instanceof InvokeCB);
  }

  @Override
  public InvokeCB cat() {
    return (InvokeCB) super.cat();
  }

  public record Data(MethodB method, CombineB args) {}

  public Data data() {
    var method = method();
    var args = args();
    validate(method.type(), args);
    return new Data(method, args);
  }

  private MethodB method() {
    return readSeqElemObj(DATA_PATH, dataHash(), CALLABLE_IDX, DATA_SEQ_SIZE, MethodB.class);
  }

  private CombineB args() {
    return readSeqElemObj(DATA_PATH, dataHash(), ARGS_IDX, DATA_SEQ_SIZE, CombineB.class);
  }
}
