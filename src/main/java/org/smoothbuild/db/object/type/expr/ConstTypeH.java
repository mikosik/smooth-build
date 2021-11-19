package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.TypeKindH.CONST;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.ConstH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.base.TypeHV;

/**
 * This class is immutable.
 */
public class ConstTypeH extends TypeHE {
  public ConstTypeH(Hash hash, TypeHV evaluationType) {
    super("CONST", hash, CONST, evaluationType);
  }

  @Override
  public ConstH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (ConstH) super.newObj(merkleRoot, objectHDb);
  }
}
