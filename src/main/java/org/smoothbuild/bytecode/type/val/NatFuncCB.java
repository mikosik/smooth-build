package org.smoothbuild.bytecode.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CatKinds.NAT_FUNC;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.hashed.Hash;

public final class NatFuncCB extends FuncCB {
  public NatFuncCB(Hash hash, FuncTB funcTB) {
    super(hash, NAT_FUNC, funcTB);
  }

  @Override
  public NatFuncB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.cat() instanceof NatFuncCB);
    return new NatFuncB(merkleRoot, bytecodeDb);
  }
}
