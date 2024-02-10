package org.smoothbuild.testing.func.nativ;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class ReturnStringStruct {
  public static ValueB func(NativeApi nativeApi, TupleB args) throws BytecodeException {
    BytecodeF factory = nativeApi.factory();
    return factory.tuple(list(factory.string("abc")));
  }
}
