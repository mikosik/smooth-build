package org.smoothbuild.virtualmachine.bytecode.expr.value;

import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

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
  public TypeB evaluationType() {
    return type();
  }

  public TypeB type() {
    return (TypeB) category();
  }
}
