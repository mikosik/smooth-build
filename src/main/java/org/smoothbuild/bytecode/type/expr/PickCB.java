package org.smoothbuild.bytecode.type.expr;

import static org.smoothbuild.bytecode.type.base.CatKindB.PICK;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.expr.PickB;
import org.smoothbuild.bytecode.type.base.ExprCatB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

/**
 * This class is immutable.
 */
public class PickCB extends ExprCatB {
  public PickCB(Hash hash, TypeB evalT) {
    super("Pick", hash, PICK, evalT);
  }

  @Override
  public PickB newObj(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    return (PickB) super.newObj(merkleRoot, byteDb);
  }
}
