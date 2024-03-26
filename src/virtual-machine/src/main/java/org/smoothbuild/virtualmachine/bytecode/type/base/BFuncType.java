package org.smoothbuild.virtualmachine.bytecode.type.base;

import static org.smoothbuild.virtualmachine.bytecode.type.base.BTypeNames.funcTypeName;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeIllegalKindException;

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
