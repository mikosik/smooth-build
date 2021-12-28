package org.smoothbuild.db.bytecode.type.expr;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.MAP;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.expr.MapB;
import org.smoothbuild.db.bytecode.type.base.ExprCatB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.bytecode.type.val.ArrayTB;
import org.smoothbuild.db.hashed.Hash;

public class MapCB extends ExprCatB {
  public MapCB(Hash hash, TypeB evalT) {
    super("Map", hash, MAP, evalT);
  }

  @Override
  public ArrayTB evalT() {
    return (ArrayTB) super.evalT();
  }

  @Override
  public MapB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (MapB) super.newObj(merkleRoot, byteDb);
  }
}
