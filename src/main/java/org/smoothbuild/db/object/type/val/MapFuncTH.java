package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.MAP_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.MapFuncH;
import org.smoothbuild.db.object.type.base.TypeH;

public class MapFuncTH extends FuncTH {
  public MapFuncTH(Hash hash, TypeH result, TupleTH paramsTuple) {
    super(hash, MAP_FUNC, result, paramsTuple);
  }

  @Override
  public MapFuncH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (MapFuncH) super.newObj(merkleRoot, objDb);
  }
}
