package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.impl.AnyTypeS;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.BlobTypeS;
import org.smoothbuild.lang.base.type.impl.BoolTypeS;
import org.smoothbuild.lang.base.type.impl.FuncTypeS;
import org.smoothbuild.lang.base.type.impl.IntTypeS;
import org.smoothbuild.lang.base.type.impl.NothingTypeS;
import org.smoothbuild.lang.base.type.impl.StringTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
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
      case AnyTypeS any -> throw new RuntimeException("S-Any cannot be converted to H-type.");
      case BlobTypeS blob -> objFactory.blobT();
      case BoolTypeS bool -> objFactory.boolT();
      case IntTypeS i -> objFactory.intT();
      case NothingTypeS n -> objFactory.nothingT();
      case StringTypeS s -> objFactory.stringT();
      case StructTypeS st -> objFactory.tupleType(map(st.fields(), isig -> visit(isig.type())));
      case VarS v ->  objFactory.var(v.name());
      case ArrayTypeS a -> objFactory.arrayT(visit(a.elem()));
      case FuncTypeS f -> objFactory.defFuncT(visit(f.res()), map(f.params(), this::visit));
    };
  }
}
