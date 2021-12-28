package org.smoothbuild.db.bytecode.obj.exc;

import org.smoothbuild.db.bytecode.type.base.CatB;
import org.smoothbuild.db.hashed.Hash;

public class DecodeObjIllegalPolymorphicTypeExc extends DecodeObjExc {
  public DecodeObjIllegalPolymorphicTypeExc(Hash hash, CatB cat) {
    super("Cannot decode %s object at %s. %s cannot be polymorphic."
        .formatted(cat.q(), hash, cat.kind()));
  }
}
