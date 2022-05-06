package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.db.Hash;

public class DecodeObjIllegalPolymorphicTypeExc extends DecodeObjExc {
  public DecodeObjIllegalPolymorphicTypeExc(Hash hash, CatB cat) {
    super("Cannot decode %s object at %s. %s cannot be polymorphic."
        .formatted(cat.q(), hash, cat.kind()));
  }
}
