package org.smoothbuild.bytecode.expr.val;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * Instance of a value.
 * This class is thread-safe.
 */
public sealed abstract class InstB extends ExprB
    permits FuncB, ArrayB, BlobB, BoolB, IntB, StringB, TupleB {
  public InstB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  @Override
  public TypeB evalT() {
    return type();
  }

  public TypeB type() {
    return (TypeB) category();
  }
}
