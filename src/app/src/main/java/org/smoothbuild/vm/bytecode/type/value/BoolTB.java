package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.BOOL;

import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public class BoolTB extends TypeB {
  public BoolTB(Hash hash) {
    super(hash, TypeNamesB.BOOL, BOOL);
  }

  @Override
  public BoolB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BoolTB);
    return new BoolB(merkleRoot, exprDb);
  }
}
