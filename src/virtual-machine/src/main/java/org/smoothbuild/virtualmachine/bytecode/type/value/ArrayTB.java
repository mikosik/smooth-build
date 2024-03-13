package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;

/**
 * This class is immutable.
 */
public final class ArrayTB extends TypeB {
  private final TypeB elem;

  public ArrayTB(Hash hash, TypeB elem) {
    super(hash, TypeNamesB.arrayTypeName(elem), ArrayB.class);
    this.elem = requireNonNull(elem);
  }

  public TypeB elem() {
    return elem;
  }

  @Override
  public ArrayB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof ArrayTB);
    return new ArrayB(merkleRoot, exprDb);
  }
}
