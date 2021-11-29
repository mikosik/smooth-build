package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.SpecKindH.REF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.type.base.ExprSpecH;
import org.smoothbuild.db.object.type.base.TypeH;

public class RefTypeH extends ExprSpecH {
  public RefTypeH(Hash hash, TypeH evaluationType) {
    super("REF", hash, REF, evaluationType);
  }

  @Override
  public RefH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (RefH) super.newObj(merkleRoot, objectHDb);
  }
}
