package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindH.MAP;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.type.base.ExprCatH;
import org.smoothbuild.db.object.type.base.TypeH;

public class MapCH extends ExprCatH {
  public MapCH(Hash hash, TypeH evalT) {
    super("Map", hash, MAP, evalT);
  }

  @Override
  public MapH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (MapH) super.newObj(merkleRoot, objDb);
  }
}
