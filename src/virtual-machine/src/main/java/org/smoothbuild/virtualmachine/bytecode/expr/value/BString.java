package org.smoothbuild.virtualmachine.bytecode.expr.value;

import static org.smoothbuild.common.base.Strings.escaped;
import static org.smoothbuild.common.base.Strings.limitedWithEllipsis;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class BString extends BValue {
  public BString(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  public String toJavaString() throws BytecodeException {
    return readData(() -> hashedDb().readString(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return limitedWithEllipsis("\"" + escaped(toJavaString()) + "\"", 30);
  }
}
