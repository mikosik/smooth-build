package org.smoothbuild.db.object.type.expr;

import static org.smoothbuild.db.object.type.base.TypeKindH.MAP;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.type.base.TypeHE;
import org.smoothbuild.db.object.type.val.ArrayTypeH;

/**
 * If represents conditional expression.
 * This class is immutable.
 */
public class MapTypeH extends TypeHE {
  public MapTypeH(Hash hash, ArrayTypeH evaluationType) {
    super("MAP", hash, MAP, evaluationType);
  }

  @Override
  public ArrayTypeH evaluationType() {
    return (ArrayTypeH) super.evaluationType();
  }

  @Override
  public MapH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (MapH) super.newObj(merkleRoot, objectHDb);
  }
}
