package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindH.IF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.type.base.ExprCatH;
import org.smoothbuild.db.object.type.base.TypeH;

public class IfCH extends ExprCatH {
  public IfCH(Hash hash, TypeH evalT) {
    super("If", hash, IF, evalT);
  }

  @Override
  public IfH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (IfH) super.newObj(merkleRoot, objDb);
  }
}
