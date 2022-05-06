package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.CatKindB.MAP;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.MapB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.db.Hash;

public class MapCB extends ExprCatB {
  public MapCB(Hash hash, TypeB evalT) {
    super(hash, "Map", MAP, evalT);
  }

  @Override
  public ArrayTB evalT() {
    return (ArrayTB) super.evalT();
  }

  @Override
  public MapB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (MapB) super.newObj(merkleRoot, objDb);
  }
}
