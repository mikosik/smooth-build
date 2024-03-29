package org.smoothbuild.vm.bytecode.expr.value;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * Instance of a value.
 * This class is thread-safe.
 */
public sealed abstract class ValueB extends ExprB
    permits FuncB, ArrayB, BlobB, BoolB, IntB, StringB, TupleB {
  public ValueB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  @Override
  public TypeB evaluationT() {
    return type();
  }

  public TypeB type() {
    return (TypeB) category();
  }
}
