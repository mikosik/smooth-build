package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBoolType;

/**
 * This class is immutable.
 */
public final class BBool extends BValue {
  public BBool(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BBoolType);
  }

  public boolean toJavaBoolean() throws BytecodeException {
    return readData(() -> hashedDb().readBoolean(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return Boolean.toString(toJavaBoolean());
  }
}
