package org.smoothbuild.virtualmachine.bytecode.kind.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet;

/**
 * This class is immutable.
 */
public final class BTupleGetKind extends BOperationKind {
  public BTupleGetKind(Hash hash, BType evaluationType) {
    super(hash, "TUPLE_GET", BTupleGet.class, evaluationType);
  }

  @Override
  public BTupleGet newExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    checkArgument(merkleRoot.kind() instanceof BTupleGetKind);
    return new BTupleGet(merkleRoot, exprDb);
  }
}
