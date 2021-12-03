package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringStruct {
  public static TupleH func(NativeApi nativeApi) {
    ObjFactory factory = nativeApi.factory();
    TupleTH type = factory.tupleT(list(factory.stringT()));
    return factory.tuple(type, list(factory.string("abc")));
  }
}
