package org.smoothbuild.bytecode.expr.inst;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.type.inst.NatFuncCB;

/**
 * Native function. Encapsulates java jar and thus java method to invoke.
 * This class is thread-safe.
 */
public final class NatFuncB extends FuncB {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int JAR_IDX = 0;
  private static final int CLASS_BINARY_NAME_IDX = 1;
  private static final int IS_PURE_IDX = 2;

  public NatFuncB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof NatFuncCB);
  }

  public BlobB jar() {
    return readDataSeqElem(JAR_IDX, DATA_SEQ_SIZE, BlobB.class);
  }

  public StringB classBinaryName() {
    return readDataSeqElem(CLASS_BINARY_NAME_IDX, DATA_SEQ_SIZE, StringB.class);
  }

  public BoolB isPure() {
    return readDataSeqElem(IS_PURE_IDX, DATA_SEQ_SIZE, BoolB.class);
  }

  @Override
  public String exprToString() {
    return "NatFunc(" + type().name() + ")";
  }
}
