package org.smoothbuild.virtualmachine.testing.func.nativ;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class ReturnStringStruct {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    BytecodeFactory factory = nativeApi.factory();
    return factory.tuple(list(factory.string("abc")));
  }
}
