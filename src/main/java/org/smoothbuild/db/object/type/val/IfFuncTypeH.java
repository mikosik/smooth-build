package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.IF_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.IfFuncH;
import org.smoothbuild.db.object.type.base.TypeH;

public class IfFuncTypeH extends FuncTypeH {
  public IfFuncTypeH(Hash hash, TypeH result, TupleTypeH paramsTuple) {
    super(hash, IF_FUNC, result, paramsTuple);
  }

  @Override
  public IfFuncH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (IfFuncH) super.newObj(merkleRoot, objDb);
  }
}
