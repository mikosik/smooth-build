package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.base.CatKindB.MAP;

import org.smoothbuild.bytecode.obj.ByteDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.MapB;
import org.smoothbuild.bytecode.type.base.ExprCatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.db.Hash;

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
