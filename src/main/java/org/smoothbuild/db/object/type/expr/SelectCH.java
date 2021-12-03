package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.CatKindH.SELECT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.type.base.ExprCatH;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * This class is immutable.
 */
public class SelectCH extends ExprCatH {
  public SelectCH(Hash hash, TypeH evalType) {
    super("Select", hash, SELECT, evalType);
  }

  @Override
  public SelectH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (SelectH) super.newObj(merkleRoot, objDb);
  }
}
