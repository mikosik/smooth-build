package org.smoothbuild.acceptance.testing;

import java.util.List;

import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.db.record.db.RecordFactory;
import org.smoothbuild.db.record.spec.TupleSpec;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class ReturnStringTuple {
  @SmoothFunction("returnStringTuple")
  public static Tuple returnStringTuple(NativeApi nativeApi) {
    RecordFactory factory = nativeApi.factory();
    TupleSpec tupleSpec = factory.tupleSpec(List.of(factory.stringSpec()));
    return factory.tuple(tupleSpec, List.of(factory.string("abc")));
  }
}
