package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.MAP_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.MapFuncH;
import org.smoothbuild.db.object.type.base.TypeH;

public class MapFuncTypeH extends FuncTypeH {
  public MapFuncTypeH(Hash hash, TypeH result, TupleTypeH paramsTuple) {
    super(hash, MAP_FUNC, result, paramsTuple);
  }

  @Override
  public MapFuncH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (MapFuncH) super.newObj(merkleRoot, objectHDb);
  }
}
