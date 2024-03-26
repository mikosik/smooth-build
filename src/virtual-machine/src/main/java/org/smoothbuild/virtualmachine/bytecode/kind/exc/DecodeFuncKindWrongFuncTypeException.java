package org.smoothbuild.virtualmachine.bytecode.kind.exc;

import static org.smoothbuild.virtualmachine.bytecode.kind.BKindDb.DATA_PATH;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.KindId;

public class DecodeFuncKindWrongFuncTypeException extends DecodeKindNodeException {
  public static DecodeFuncKindWrongFuncTypeException illegalMapFuncTypeExc(
      Hash hash, BFuncType funcType) {
    return new DecodeFuncKindWrongFuncTypeException(
        hash,
        KindId.MAP_FUNC,
        "Function type " + funcType.q() + " doesn't match type of `map` function.");
  }

  public DecodeFuncKindWrongFuncTypeException(Hash hash, KindId kindId, String message) {
    super(hash, kindId, DATA_PATH, message);
  }
}
