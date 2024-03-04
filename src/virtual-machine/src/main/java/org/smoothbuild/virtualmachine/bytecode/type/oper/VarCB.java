package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.VAR;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.VarB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class VarCB extends OperCB {
  public VarCB(Hash hash, TypeB evaluationType) {
    super(hash, VAR, evaluationType);
  }

  @Override
  public VarB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof VarCB);
    return new VarB(merkleRoot, exprDb);
  }
}
