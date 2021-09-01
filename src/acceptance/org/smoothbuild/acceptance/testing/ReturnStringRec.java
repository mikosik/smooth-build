package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringRec {
  public static Rec function(NativeApi nativeApi) {
    ObjectFactory factory = nativeApi.factory();
    RecSpec recSpec = factory.recSpec(list(factory.stringSpec()));
    return factory.rec(recSpec, list(factory.string("abc")));
  }
}
