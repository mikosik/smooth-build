package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BMap;

public final class BMapCategory extends BFuncCategory {
  public BMapCategory(Hash hash, BFuncType funcType) {
    super(hash, "MAP", funcType, BMap.class);
  }

  @Override
  public BMap newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BMapCategory);
    return new BMap(merkleRoot, exprDb);
  }

  @Override
  public boolean containsData() {
    return false;
  }
}
