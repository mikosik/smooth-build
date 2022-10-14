package org.smoothbuild.bytecode.expr.inst;

import static org.smoothbuild.util.Strings.escaped;
import static org.smoothbuild.util.Strings.limitedWithEllipsis;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;

/**
 * This class is thread-safe.
 */
public final class StringB extends InstB {
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
