package org.smoothbuild.vm.bytecode.expr.value;

import static org.smoothbuild.util.Strings.escaped;
import static org.smoothbuild.util.Strings.limitedWithEllipsis;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class StringB extends ValueB {
  public StringB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  public String toJ() {
    return readData(() -> hashedDb().readString(dataHash()));
  }

  @Override
  public String exprToString() {
    return limitedWithEllipsis("\"" + escaped(toJ()) + "\"", 30);
  }
}
