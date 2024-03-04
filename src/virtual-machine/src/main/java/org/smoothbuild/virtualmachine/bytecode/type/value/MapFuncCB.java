package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.MAP_FUNC;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.MapFuncB;

public final class MapFuncCB extends FuncCB {
  public MapFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, MAP_FUNC, funcTB);
  }

  @Override
  public MapFuncB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof MapFuncCB);
    return new MapFuncB(merkleRoot, exprDb);
  }

  @Override
  public boolean containsData() {
    return false;
  }
}
