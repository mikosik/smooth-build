package org.smoothbuild.vm.bytecode.type.value;

import static org.smoothbuild.vm.bytecode.type.CategoryKinds.FUNC;
import static org.smoothbuild.vm.bytecode.type.value.TypeNamesB.funcTypeName;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprFuncIsIllegalCatExc;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public final class FuncTB extends TypeB {
  private final TupleTB params;
  private final TypeB result;

  public FuncTB(Hash hash, TupleTB params, TypeB result) {
    super(hash, funcTypeName(params.elements(), result), FUNC);
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
