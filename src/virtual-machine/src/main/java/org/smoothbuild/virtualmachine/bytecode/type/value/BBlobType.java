package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;

/**
 * This class is immutable.
 */
public class BBlobType extends BType {
  public BBlobType(Hash hash) {
    super(hash, BTypeNames.BLOB, BBlob.class);
  }

  @Override
  public BBlob newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof BBlobType);
    return new BBlob(merkleRoot, exprDb);
  }
}
