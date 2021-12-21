package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatB;
import org.smoothbuild.db.object.type.val.FuncTB;

public class DecodeMapIllegalMappingFuncExc extends DecodeObjExc {
  public DecodeMapIllegalMappingFuncExc(Hash hash, CatB cat, FuncTB funcT) {
    super(buildMessage(hash, cat, funcT));
  }

  private static String buildMessage(Hash hash, CatB cat, FuncTB funcT) {
    return ("Cannot decode %s object at %s. Its 'func' component type is %s while expected "
        + "function with one parameter.")
        .formatted(cat.q(), hash, funcT.q());
  }
}
