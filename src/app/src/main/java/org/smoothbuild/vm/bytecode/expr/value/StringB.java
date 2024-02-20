package org.smoothbuild.vm.bytecode.expr.value;

import static org.smoothbuild.common.Strings.escaped;
import static org.smoothbuild.common.Strings.limitedWithEllipsis;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class StringB extends ValueB {
  public StringB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  public String toJ() throws BytecodeException {
    return readData(() -> hashedDb().readString(dataHash()));
  }

  @Override
  public String exprToString() throws BytecodeException {
    return limitedWithEllipsis("\"" + escaped(toJ()) + "\"", 30);
  }
}
