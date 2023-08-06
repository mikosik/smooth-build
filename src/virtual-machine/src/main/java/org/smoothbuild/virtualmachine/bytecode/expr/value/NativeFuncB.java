package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.value.NativeFuncCB;

/**
 * Native function. Encapsulates java jar and thus java method to invoke.
 * This class is thread-safe.
 */
public final class NativeFuncB extends FuncB {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int JAR_IDX = 0;
  private static final int CLASS_BINARY_NAME_IDX = 1;
  private static final int IS_PURE_IDX = 2;

  public NativeFuncB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.category() instanceof NativeFuncCB);
  }

  public BlobB jar() throws BytecodeException {
    return readDataSeqElem(JAR_IDX, DATA_SEQ_SIZE, BlobB.class);
  }

  public StringB classBinaryName() throws BytecodeException {
    return readDataSeqElem(CLASS_BINARY_NAME_IDX, DATA_SEQ_SIZE, StringB.class);
  }

  public BoolB isPure() throws BytecodeException {
    return readDataSeqElem(IS_PURE_IDX, DATA_SEQ_SIZE, BoolB.class);
  }

  @Override
  public String exprToString() {
    return "NativeFunc(" + type().name() + ")";
  }
}
