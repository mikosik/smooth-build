package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.SpecKindH.PARAM_REF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.type.base.ExprSpecH;
import org.smoothbuild.db.object.type.base.TypeH;

public class ParamRefTypeH extends ExprSpecH {
  public ParamRefTypeH(Hash hash, TypeH evalType) {
    super("ParamRef", hash, PARAM_REF, evalType);
  }

  @Override
  public ParamRefH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (ParamRefH) super.newObj(merkleRoot, objDb);
  }
}
