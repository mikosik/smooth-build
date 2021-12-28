package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatB;

public class DecodeObjIllegalPolymorphicTypeExc extends DecodeObjExc {
  public DecodeObjIllegalPolymorphicTypeExc(Hash hash, CatB cat) {
    super("Cannot decode %s object at %s. %s cannot be polymorphic."
        .formatted(cat.q(), hash, cat.kind()));
  }
}
