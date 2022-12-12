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
  private final TypeB result;

  public FuncTB(Hash hash, TupleTB params, TypeB result) {
    super(hash, funcTypeName(params.items(), result), FUNC);
    this.params = params;
    this.result = result;
  }

  public TupleTB params() {
    return params;
  }

  public TypeB result() {
    return result;
  }

  @Override
  public ExprB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    throw new DecodeExprFuncIsIllegalCatExc(merkleRoot.hash(), this);
  }
}
