package org.smoothbuild.virtualmachine.bytecode.type.exc;

import static org.smoothbuild.virtualmachine.bytecode.type.BKindDb.DATA_PATH;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.IF_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.MAP_FUNC;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.KindId;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;

public class DecodeFuncKindWrongFuncTypeException extends DecodeKindNodeException {
  public static DecodeFuncKindWrongFuncTypeException illegalIfFuncTypeExc(
      Hash hash, BFuncType funcType) {
    return new DecodeFuncKindWrongFuncTypeException(
        hash, IF_FUNC, "Function type " + funcType.q() + " doesn't match type of `if` function.");
  }

  public static DecodeFuncKindWrongFuncTypeException illegalMapFuncTypeExc(
      Hash hash, BFuncType funcType) {
    return new DecodeFuncKindWrongFuncTypeException(
        hash, MAP_FUNC, "Function type " + funcType.q() + " doesn't match type of `map` function.");
  }

  public DecodeFuncKindWrongFuncTypeException(Hash hash, KindId kindId, String message) {
    super(hash, kindId, DATA_PATH, message);
  }
}
