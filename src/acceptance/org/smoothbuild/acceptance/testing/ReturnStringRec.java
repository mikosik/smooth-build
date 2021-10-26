package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringRec {
  public static Struc_ function(NativeApi nativeApi) {
    ObjectFactory factory = nativeApi.factory();
    StructSpec recSpec =
        factory.structSpec("StringHolder", namedList(list(named("field", factory.stringSpec()))));
    return factory.struct(recSpec, list(factory.string("abc")));
  }
}
