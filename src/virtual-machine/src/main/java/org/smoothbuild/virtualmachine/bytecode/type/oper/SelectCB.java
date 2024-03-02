package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.SELECT;

import org.smoothbuild.common.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class SelectCB extends OperCB {
  public SelectCB(Hash hash, TypeB evaluationType) {
    super(hash, SELECT, evaluationType);
  }

  @Override
  public SelectB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof SelectCB);
    return new SelectB(merkleRoot, exprDb);
  }
}
