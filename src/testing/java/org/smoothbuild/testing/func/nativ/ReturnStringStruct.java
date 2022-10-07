package org.smoothbuild.testing.func.nativ;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringStruct {
  public static InstB func(NativeApi nativeApi, TupleB args) {
    BytecodeF factory = nativeApi.factory();
    return factory.tuple(list(factory.string("abc")));
  }
}
