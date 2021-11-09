package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringStruct {
  public static TupleH function(NativeApi nativeApi) {
    ObjFactory factory = nativeApi.factory();
    TupleTypeH type = factory.tupleType(list(factory.stringType()));
    return factory.tuple(type, list(factory.string("abc")));
  }
}
