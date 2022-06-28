package org.smoothbuild.testing.func.bytecode;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.Map;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.type.cnst.TypeB;

public class ReturnReturnAbcFunc {
  public static ObjB bytecode(BytecodeF f, Map<String, TypeB> varMap) {
    var type = f.funcT(f.stringT(), list());
    return f.func(type, f.string("abc"));
  }
}