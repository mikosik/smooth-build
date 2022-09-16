package org.smoothbuild.bytecode.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.bytecode.type.CatKinds.ARRAY;
import static org.smoothbuild.bytecode.type.val.TNamesB.arrayTypeName;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public final class ArrayTB extends TypeB {
  private final TypeB elem;

  public ArrayTB(Hash hash, TypeB elem) {
    super(hash, arrayTypeName(elem), ARRAY);
    this.elem = requireNonNull(elem);
  }

  public TypeB elem() {
    return elem;
  }

  @Override
  public ArrayB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.cat() instanceof ArrayTB);
    return new ArrayB(merkleRoot, bytecodeDb);
  }
}
