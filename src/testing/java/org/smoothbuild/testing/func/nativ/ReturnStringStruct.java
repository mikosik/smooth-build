package org.smoothbuild.testing.func.nativ;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringStruct {
  public static TupleB func(NativeApi nativeApi) {
    BytecodeF factory = nativeApi.factory();
    TupleTB type = factory.tupleT(list(factory.stringT()));
    return factory.tuple(type, list(factory.string("abc")));
  }
}
