package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.TypeKindH.REF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.base.TypeHV;

public class RefTypeH extends TypeHE {
  public RefTypeH(Hash hash, TypeHV evaluationType) {
    super("REF", hash, REF, evaluationType);
  }

  @Override
  public RefH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (RefH) super.newObj(merkleRoot, objectHDb);
  }
}
