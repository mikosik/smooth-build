package org.smoothbuild.virtualmachine.bytecode.type.exc;

import static org.smoothbuild.virtualmachine.bytecode.type.CategoryDb.DATA_PATH;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.IF_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.MAP_FUNC;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryId;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;

public class DecodeFuncCatWrongFuncTypeException extends DecodeCatNodeException {
  public static DecodeFuncCatWrongFuncTypeException illegalIfFuncTypeExc(Hash hash, FuncTB funcTB) {
    return new DecodeFuncCatWrongFuncTypeException(
        hash, IF_FUNC, "Function type " + funcTB.q() + " doesn't match type of `if` function.");
  }

  public static DecodeFuncCatWrongFuncTypeException illegalMapFuncTypeExc(
      Hash hash, FuncTB funcTB) {
    return new DecodeFuncCatWrongFuncTypeException(
        hash, MAP_FUNC, "Function type " + funcTB.q() + " doesn't match type of `map` function.");
  }

  public DecodeFuncCatWrongFuncTypeException(Hash hash, CategoryId categoryId, String message) {
    super(hash, categoryId, DATA_PATH, message);
  }
}
