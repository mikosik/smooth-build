package org.smoothbuild.bytecode.type.oper;

import static org.smoothbuild.bytecode.type.CatKindB.SELECT;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * This class is immutable.
 */
public class SelectCB extends OperCatB {
  public SelectCB(Hash hash, TypeB evalT) {
    super(hash, "Select", SELECT, evalT);
  }

  @Override
  public SelectB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (SelectB) super.newObj(merkleRoot, bytecodeDb);
  }
}
