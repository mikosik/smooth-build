package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindB.MAP;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.MapB;
import org.smoothbuild.db.object.type.base.ExprCatB;
import org.smoothbuild.db.object.type.base.TypeB;

public class MapCB extends ExprCatB {
  public MapCB(Hash hash, TypeB evalT) {
    super("Map", hash, MAP, evalT);
  }

  @Override
  public MapB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (MapB) super.newObj(merkleRoot, byteDb);
  }
}
