package org.smoothbuild.bytecode.expr.val;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * Value.
 * This class is thread-safe.
 */
public sealed abstract class ValB extends ExprB
    permits FuncB, ArrayB, BlobB, BoolB, IntB, StringB, TupleB {
  public ValB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  @Override
  public TypeB type() {
    return (TypeB) cat();
  }
}
