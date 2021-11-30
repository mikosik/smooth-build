package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.DEF_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.DefFuncH;
import org.smoothbuild.db.object.type.base.TypeH;

public class DefFuncTypeH extends FuncTypeH {
  public DefFuncTypeH(Hash hash, TypeH result, TupleTypeH paramsTuple) {
    super(hash, DEF_FUNC, result, paramsTuple);
  }

  @Override
  public DefFuncH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (DefFuncH) super.newObj(merkleRoot, objDb);
  }
}
