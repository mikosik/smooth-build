package org.smoothbuild.vm.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.MAP_FUNC;

import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.value.MapFuncB;
import org.smoothbuild.vm.bytecode.hashed.Hash;

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
