package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.base.BNativeFuncKind;

/**
 * Native function. Encapsulates java jar and thus java method to invoke.
 * This class is thread-safe.
 */
public final class BNativeFunc extends BFunc {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int JAR_IDX = 0;
  private static final int CLASS_BINARY_NAME_IDX = 1;
  private static final int IS_PURE_IDX = 2;

  public BNativeFunc(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BNativeFuncKind);
  }

  public BBlob jar() throws BytecodeException {
    return readElementFromDataAsInstanceChain(JAR_IDX, DATA_SEQ_SIZE, BBlob.class);
  }

  public BString classBinaryName() throws BytecodeException {
    return readElementFromDataAsInstanceChain(CLASS_BINARY_NAME_IDX, DATA_SEQ_SIZE, BString.class);
  }

  public BBool isPure() throws BytecodeException {
    return readElementFromDataAsInstanceChain(IS_PURE_IDX, DATA_SEQ_SIZE, BBool.class);
  }

  @Override
  public String exprToString() {
    return "NativeFunc(" + type().name() + ")";
  }
}
