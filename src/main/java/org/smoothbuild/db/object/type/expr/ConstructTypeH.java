package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.TypeKindH.CONSTRUCT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.val.TupleTypeH;

/**
 * This class is immutable.
 */
public class ConstructTypeH extends TypeHE {
  public ConstructTypeH(Hash hash, TupleTypeH evaluationType) {
    super("CONSTRUCT", hash, CONSTRUCT, evaluationType);
  }

  @Override
  public TupleTypeH evaluationType() {
    return (TupleTypeH) super.evaluationType();
  }

  @Override
  public ConstructH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (ConstructH) super.newObj(merkleRoot, objectHDb);
  }
}
