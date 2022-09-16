package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKinds.FUNC;
import static org.smoothbuild.bytecode.type.val.TNamesB.funcTypeName;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprFuncIsIllegalCatExc;
import org.smoothbuild.bytecode.hashed.Hash;

public final class FuncTB extends TypeB {
  private final TypeB res;
  private final TupleTB params;

  public FuncTB(Hash hash, TypeB res, TupleTB params) {
    super(hash, funcTypeName(res, params.items()), FUNC);
    this.res = res;
    this.params = params;
  }

  public TypeB res() {
    return res;
  }

  public TupleTB params() {
    return params;
  }

  @Override
  public ExprB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    throw new DecodeExprFuncIsIllegalCatExc(merkleRoot.hash(), this);
  }
}
