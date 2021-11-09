package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.TypeKindH.SELECT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.base.TypeHV;

/**
 * This class is immutable.
 */
public class SelectTypeH extends TypeHE {
  public SelectTypeH(Hash hash, TypeHV evaluationType) {
    super("SELECT", hash, SELECT, evaluationType);
  }

  @Override
  public SelectH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new SelectH(merkleRoot, objectHDb);
  }
}
