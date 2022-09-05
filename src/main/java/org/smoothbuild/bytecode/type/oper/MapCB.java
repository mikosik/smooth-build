package org.smoothbuild.bytecode.type.oper;

import static org.smoothbuild.bytecode.type.CatKindB.MAP;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.MapB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.db.Hash;

public class MapCB extends OperCatB {
  public MapCB(Hash hash, TypeB evalT) {
    super(hash, "Map", MAP, evalT);
  }

  @Override
  public ArrayTB evalT() {
    return (ArrayTB) super.evalT();
  }

  @Override
  public MapB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (MapB) super.newObj(merkleRoot, bytecodeDb);
  }
}
