package org.smoothbuild.vm.bytecode.expr.value;

import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * Instance of a value.
 * This class is thread-safe.
 */
public abstract sealed class ValueB extends ExprB
    permits FuncB, ArrayB, BlobB, BoolB, IntB, StringB, TupleB {
  public ValueB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
  }

  @Override
  public TypeB evaluationT() {
    return type();
  }

  public TypeB type() {
    return (TypeB) category();
  }
}
