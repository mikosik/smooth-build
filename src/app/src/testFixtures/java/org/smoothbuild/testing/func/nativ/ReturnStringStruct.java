package org.smoothbuild.testing.func.nativ;

import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

import io.vavr.collection.Array;

public class ReturnStringStruct {
  public static ValueB func(NativeApi nativeApi, TupleB args) {
    BytecodeF factory = nativeApi.factory();
    return factory.tuple(Array.of(factory.string("abc")));
  }
}
