package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.CatKindB.SELECT;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.SelectB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class SelectCB extends ExprCatB {
  public SelectCB(Hash hash, TypeB evalT) {
    super(hash, "Select", SELECT, evalT);
  }

  @Override
  public SelectB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (SelectB) super.newObj(merkleRoot, objDb);
  }
}
