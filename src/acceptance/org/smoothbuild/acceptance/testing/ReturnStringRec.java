package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringRec {
  public static Struc_ function(NativeApi nativeApi) {
    ObjectFactory factory = nativeApi.factory();
    StructSpec recSpec =
        factory.structSpec("StringHolder", list(factory.stringSpec()), list("field"));
    return factory.struct(recSpec, list(factory.string("abc")));
  }
}
