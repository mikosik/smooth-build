package org.smoothbuild.virtualmachine.bytecode.type.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;

/**
 * This class is immutable.
 */
public class StringTB extends TypeB {
  public StringTB(Hash hash) {
    super(hash, TypeNamesB.STRING, StringB.class);
  }

  @Override
  public StringB newExpr(MerkleRoot merkleRoot, ExprDb exprDb) {
    checkArgument(merkleRoot.category() instanceof StringTB);
    return new StringB(merkleRoot, exprDb);
  }
}
