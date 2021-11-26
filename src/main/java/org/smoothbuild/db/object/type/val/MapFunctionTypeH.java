package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.MAP_FUNCTION;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.MapFunctionH;
import org.smoothbuild.db.object.type.base.TypeHV;

public class MapFunctionTypeH extends FunctionTypeH {
  public MapFunctionTypeH(Hash hash, TypeHV result, TupleTypeH paramsTuple) {
    super(hash, MAP_FUNCTION, result, paramsTuple);
  }

  @Override
  public MapFunctionH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (MapFunctionH) super.newObj(merkleRoot, objectHDb);
  }
}
