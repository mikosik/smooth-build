package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.STRING;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class StringTB extends BaseTB {
  public StringTB(Hash hash) {
    super(hash, TNamesB.STRING, STRING);
  }

  @Override
  public StringB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (StringB) super.newObj(merkleRoot, bytecodeDb);
  }
}
