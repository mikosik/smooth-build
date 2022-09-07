package org.smoothbuild.bytecode.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CatKindB.INT;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.hashed.Hash;

/**
 * This class is immutable.
 */
public class IntTB extends BaseTB {
  public IntTB(Hash hash) {
    super(hash, TNamesB.INT, INT);
  }

  @Override
  public IntB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.cat() instanceof IntTB);
    return new IntB(merkleRoot, bytecodeDb);
  }
}
