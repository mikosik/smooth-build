package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.MethodB;
import org.smoothbuild.bytecode.type.oper.InvokeCB;

/**
 * Native call expression. It invokes java method represented by MethodH.
 *
 * This class is thread-safe.
 */
public final class InvokeB extends CallableB {
  public InvokeB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
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
    return readSeqElemExpr(DATA_PATH, dataHash(), CALLABLE_IDX, DATA_SEQ_SIZE, MethodB.class);
  }

  private CombineB args() {
    return readSeqElemExpr(DATA_PATH, dataHash(), ARGS_IDX, DATA_SEQ_SIZE, CombineB.class);
  }
}
