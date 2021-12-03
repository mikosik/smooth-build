package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.NAT_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.type.base.TypeH;

public class NatFuncTH extends FuncTH {
  public NatFuncTH(Hash hash, TypeH result, TupleTH paramsTuple) {
    super(hash, NAT_FUNC, result, paramsTuple);
  }

  @Override
  public NatFuncH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (NatFuncH) super.newObj(merkleRoot, objDb);
  }
}
