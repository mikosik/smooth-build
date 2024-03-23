package org.smoothbuild.virtualmachine.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BPick;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * This class is immutable.
 */
public class BPickCategory extends BOperCategory {
  public BPickCategory(Hash hash, BType evaluationType) {
    super(hash, "PICK", BPick.class, evaluationType);
  }

  @Override
  public BPick newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BPickCategory);
    return new BPick(merkleRoot, exprDb);
  }
}
