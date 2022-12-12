package org.smoothbuild.bytecode.type.value;

import static org.smoothbuild.bytecode.type.CategoryKinds.FUNC;
import static org.smoothbuild.bytecode.type.value.ValidNamesB.funcTypeName;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprFuncIsIllegalCatExc;
import org.smoothbuild.bytecode.hashed.Hash;

public final class FuncTB extends TypeB {
  private final TupleTB params;
  private final TypeB res;

  public FuncTB(Hash hash, TupleTB params, TypeB res) {
    super(hash, funcTypeName(params.items(), res), FUNC);
    this.params = params;
    this.res = res;
  }

  public TupleTB params() {
    return params;
  }

  public TypeB res() {
    return res;
  }

  @Override
  public ExprB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    throw new DecodeExprFuncIsIllegalCatExc(merkleRoot.hash(), this);
  }
}
