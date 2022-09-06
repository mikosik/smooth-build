package org.smoothbuild.bytecode.type.oper;

import static org.smoothbuild.bytecode.type.CatKindB.COMBINE;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TupleTB;

/**
 * This class is immutable.
 */
public class CombineCB extends OperCatB {
  public CombineCB(Hash hash, TupleTB evalT) {
    super(hash, "Combine", COMBINE, evalT);
  }

  @Override
  public TupleTB evalT() {
    return (TupleTB) super.evalT();
  }

  @Override
  public CombineB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (CombineB) super.newObj(merkleRoot, bytecodeDb);
  }
}