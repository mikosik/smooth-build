package org.smoothbuild.db.object.type.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.TypeKindH.MAP;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.base.TypeHV;

/**
 * If represents conditional expression.
 * This class is immutable.
 */
public class MapTypeH extends TypeHE {
  public MapTypeH(Hash hash, TypeHV evaluationType) {
    super("MAP", hash, MAP, evaluationType);
  }

  @Override
  public MapH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new MapH(merkleRoot, objectHDb);
  }
}
