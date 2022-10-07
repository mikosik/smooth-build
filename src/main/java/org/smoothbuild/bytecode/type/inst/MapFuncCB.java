package org.smoothbuild.bytecode.type.inst;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.MAP_FUNC;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.inst.MapFuncB;
import org.smoothbuild.bytecode.hashed.Hash;

public final class MapFuncCB extends FuncCB {
  public MapFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, MAP_FUNC, funcTB);
  }

  @Override
  public MapFuncB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof MapFuncCB);
    return new MapFuncB(merkleRoot, bytecodeDb);
  }

  @Override
  public boolean containsData() {
    return false;
  }
}
