package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.bytecode.ObjFactory;
import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.db.bytecode.type.val.TupleTB;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringStruct {
  public static TupleB func(NativeApi nativeApi) {
    ObjFactory factory = nativeApi.factory();
    TupleTB type = factory.tupleT(list(factory.stringT()));
    return factory.tuple(type, list(factory.string("abc")));
  }
}
