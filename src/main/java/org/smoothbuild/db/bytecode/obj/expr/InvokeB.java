package org.smoothbuild.db.bytecode.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.val.MethodB;
import org.smoothbuild.db.bytecode.type.expr.InvokeCB;

/**
 * Native call expression. It invokes java method represented by MethodH.
 *
 * This class is thread-safe.
 */
public final class InvokeB extends CallLikeB {
  public InvokeB(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
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
