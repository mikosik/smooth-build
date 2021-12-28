package org.smoothbuild.db.bytecode.type.expr;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.PARAM_REF;

import org.smoothbuild.db.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.db.bytecode.type.base.ExprCatB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;

public class ParamRefCB extends ExprCatB {
  public ParamRefCB(Hash hash, TypeB evalT) {
    super("ParamRef", hash, PARAM_REF, evalT);
  }

  @Override
  public ParamRefB newObj(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    return (ParamRefB) super.newObj(merkleRoot, byteDb);
  }
}
