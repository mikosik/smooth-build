package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CategoryB;
import org.smoothbuild.bytecode.type.val.FuncTB;

public class DecodeMapIllegalMappingFuncExc extends DecodeExprExc {
  public DecodeMapIllegalMappingFuncExc(Hash hash, CategoryB cat, FuncTB funcT) {
    super(buildMessage(hash, cat, funcT));
  }

  private static String buildMessage(Hash hash, CategoryB cat, FuncTB funcT) {
    return ("Cannot decode %s object at %s. Its 'func' component type is %s while expected "
        + "function with one parameter.")
        .formatted(cat.q(), hash, funcT.q());
  }
}
