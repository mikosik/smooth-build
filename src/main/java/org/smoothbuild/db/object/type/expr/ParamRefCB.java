package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindB.PARAM_REF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.ParamRefB;
import org.smoothbuild.db.object.type.base.ExprCatB;
import org.smoothbuild.db.object.type.base.TypeB;

public class ParamRefCB extends ExprCatB {
  public ParamRefCB(Hash hash, TypeB evalT) {
    super("ParamRef", hash, PARAM_REF, evalT);
  }

  @Override
  public ParamRefB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (ParamRefB) super.newObj(merkleRoot, byteDb);
  }
}
