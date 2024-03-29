package org.smoothbuild.vm.bytecode.type.exc;

import static org.smoothbuild.vm.bytecode.type.CategoryDb.DATA_PATH;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.IF_FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.MAP_FUNC;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryKindB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;

public class DecodeFuncCatWrongFuncTypeExc extends DecodeCatNodeExc {
  public static DecodeFuncCatWrongFuncTypeExc illegalIfFuncTypeExc(Hash hash, FuncTB funcTB) {
    return new DecodeFuncCatWrongFuncTypeExc(hash, IF_FUNC,
        "Function type " + funcTB.q() + " doesn't match type of `if` function.");
  }

  public static DecodeFuncCatWrongFuncTypeExc illegalMapFuncTypeExc(Hash hash, FuncTB funcTB) {
    return new DecodeFuncCatWrongFuncTypeExc(hash, MAP_FUNC,
        "Function type " + funcTB.q() + " doesn't match type of `map` function.");
  }

  public DecodeFuncCatWrongFuncTypeExc(Hash hash, CategoryKindB kind, String message) {
    super(hash, kind, DATA_PATH, message);
  }
}
