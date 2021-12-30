package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.db.Hash;

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
