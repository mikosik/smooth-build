package org.smoothbuild.acceptance.testing;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.type.val.StructOType;
import org.smoothbuild.plugin.NativeApi;

public class ReturnStringStruct {
  public static Struc_ function(NativeApi nativeApi) {
    ObjFactory factory = nativeApi.factory();
    StructOType type =
        factory.structType("StringHolder", namedList(list(named("field", factory.stringType()))));
    return factory.struct(type, list(factory.string("abc")));
  }
}
