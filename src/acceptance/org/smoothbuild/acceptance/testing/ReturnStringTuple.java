package org.smoothbuild.acceptance.testing;

import java.util.List;

import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringTuple {
  public static Tuple function(NativeApi nativeApi) {
    ObjectFactory factory = nativeApi.factory();
    TupleSpec tupleSpec = factory.tupleSpec(List.of(factory.stringSpec()));
    return factory.tuple(tupleSpec, List.of(factory.string("abc")));
  }
}
