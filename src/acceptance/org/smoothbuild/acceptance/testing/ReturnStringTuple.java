package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringTuple {
  public static Tuple function(NativeApi nativeApi) {
    ObjectFactory factory = nativeApi.factory();
    TupleSpec tupleSpec = factory.tupleSpec(list(factory.stringSpec()));
    return factory.tuple(tupleSpec, list(factory.string("abc")));
  }
}
