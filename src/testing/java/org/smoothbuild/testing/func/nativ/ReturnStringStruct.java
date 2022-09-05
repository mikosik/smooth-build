package org.smoothbuild.testing.func.nativ;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringStruct {
  public static ValB func(NativeApi nativeApi, TupleB args) {
    BytecodeF factory = nativeApi.factory();
    return factory.tuple(list(factory.string("abc")));
  }
}
