package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.ARRAY;

import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public final class ArrayTB extends TypeB {
  private final TypeB elem;

  public ArrayTB(Hash hash, TypeB elem) {
    super(hash, TypeNamesB.arrayTypeName(elem), ARRAY);
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
