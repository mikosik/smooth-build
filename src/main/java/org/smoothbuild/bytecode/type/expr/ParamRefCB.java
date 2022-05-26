package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.CatKindB.PARAM_REF;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.ParamRefB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.db.Hash;

public class ParamRefCB extends ExprCatB {
  public ParamRefCB(Hash hash, TypeB evalT) {
    super(hash, "ParamRef", PARAM_REF, evalT);
  }

  @Override
  public ParamRefB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (ParamRefB) super.newObj(merkleRoot, objDb);
  }
}
