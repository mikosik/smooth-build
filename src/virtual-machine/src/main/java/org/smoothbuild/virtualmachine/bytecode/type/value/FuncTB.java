package org.smoothbuild.virtualmachine.bytecode.type.value;

import static org.smoothbuild.virtualmachine.bytecode.type.value.TypeNamesB.funcTypeName;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeIllegalCategoryException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.FuncB;

public final class FuncTB extends TypeB {
  private final TupleTB params;
  private final TypeB result;

  public FuncTB(Hash hash, TupleTB params, TypeB result) {
    super(hash, funcTypeName(params.elements(), result), FuncB.class);
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
  public ExprB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) throws DecodeIllegalCategoryException {
    throw new DecodeIllegalCategoryException(merkleRoot.hash(), this);
  }
}
