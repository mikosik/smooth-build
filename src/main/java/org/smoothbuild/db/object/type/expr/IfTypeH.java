package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.TypeKindH.IF;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.base.TypeHV;

/**
 * If represents conditional expression.
 * This class is immutable.
 */
public class IfTypeH extends TypeHE {
  public IfTypeH(Hash hash, TypeHV evaluationType) {
    super("IF", hash, IF, evaluationType);
  }

  @Override
  public IfH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new IfH(merkleRoot, objectHDb);
  }
}
