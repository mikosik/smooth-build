package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.SpecKindH.PARAM_REF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.type.base.ExprSpecH;
import org.smoothbuild.db.object.type.base.TypeH;

public class RefTypeH extends ExprSpecH {
  public RefTypeH(Hash hash, TypeH evaluationType) {
    super("REF", hash, PARAM_REF, evaluationType);
  }

  @Override
  public ParamRefH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (ParamRefH) super.newObj(merkleRoot, objDb);
  }
}
