package org.smoothbuild.bytecode.type.inst;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.STRING;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.inst.StringB;
import org.smoothbuild.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public class StringTB extends BaseTB {
  public StringTB(Hash hash) {
    super(hash, TypeNamesB.STRING, STRING);
  }

  @Override
  public StringB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof StringTB);
    return new StringB(merkleRoot, bytecodeDb);
  }
}
