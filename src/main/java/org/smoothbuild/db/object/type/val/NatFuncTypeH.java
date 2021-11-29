package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.NAT_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.type.base.TypeH;

public class NatFuncTypeH extends FuncTypeH {
  public NatFuncTypeH(Hash hash, TypeH result, TupleTypeH paramsTuple) {
    super(hash, NAT_FUNC, result, paramsTuple);
  }

  @Override
  public NatFuncH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (NatFuncH) super.newObj(merkleRoot, objectHDb);
  }
}
