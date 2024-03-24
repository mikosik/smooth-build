package org.smoothbuild.virtualmachine.bytecode.type.value;

import static org.smoothbuild.virtualmachine.bytecode.type.value.BTypeNames.funcTypeName;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeIllegalKindException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BFunc;

public final class BFuncType extends BType {
  private final BTupleType params;
  private final BType result;

  public BFuncType(Hash hash, BTupleType params, BType result) {
    super(hash, funcTypeName(params.elements(), result), BFunc.class);
    this.params = params;
    this.result = result;
  }

  public BTupleType params() {
    return params;
  }

  public BType result() {
    return result;
  }

  @Override
  public BExpr newExpr(MerkleRoot merkleRoot, BExprDb exprDb) throws DecodeIllegalKindException {
    throw new DecodeIllegalKindException(merkleRoot.hash(), this);
  }
}
