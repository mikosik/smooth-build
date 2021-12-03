package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.impl.AnyTS;
import org.smoothbuild.lang.base.type.impl.ArrayTS;
import org.smoothbuild.lang.base.type.impl.BlobTS;
import org.smoothbuild.lang.base.type.impl.BoolTS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.IntTS;
import org.smoothbuild.lang.base.type.impl.NothingTS;
import org.smoothbuild.lang.base.type.impl.StringTS;
import org.smoothbuild.lang.base.type.impl.StructTS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.VarS;

public class TypeShConv {
  private final ObjFactory objFactory;

  @Inject
  public TypeShConv(ObjFactory objFactory) {
    this.objFactory = objFactory;
  }

  public TypeH visit(TypeS type) {
    return switch (type) {
      case AnyTS any -> throw new RuntimeException("S-Any cannot be converted to H-type.");
      case BlobTS blob -> objFactory.blobT();
      case BoolTS bool -> objFactory.boolT();
      case IntTS i -> objFactory.intT();
      case NothingTS n -> objFactory.nothingT();
      case StringTS s -> objFactory.stringT();
      case StructTS st -> objFactory.tupleT(map(st.fields(), isig -> visit(isig.type())));
      case VarS v ->  objFactory.var(v.name());
      case ArrayTS a -> objFactory.arrayT(visit(a.elem()));
      case FuncTS f -> objFactory.defFuncT(visit(f.res()), map(f.params(), this::visit));
    };
  }
}
