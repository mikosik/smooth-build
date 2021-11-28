package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.lang.base.type.impl.AnyTypeS;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.BlobTypeS;
import org.smoothbuild.lang.base.type.impl.BoolTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.IntTypeS;
import org.smoothbuild.lang.base.type.impl.NothingTypeS;
import org.smoothbuild.lang.base.type.impl.StringTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.VariableS;

public class TypeShConverter {
  private final ObjFactory objFactory;

  @Inject
  public TypeShConverter(ObjFactory objFactory) {
    this.objFactory = objFactory;
  }

  public TypeHV visit(TypeS type) {
    return switch (type) {
      case AnyTypeS any -> throw new RuntimeException("S-Any cannot be converted to H-type.");
      case BlobTypeS blob -> objFactory.blobT();
      case BoolTypeS bool -> objFactory.boolT();
      case IntTypeS i -> objFactory.intT();
      case NothingTypeS n -> objFactory.nothingT();
      case StringTypeS s -> objFactory.stringT();
      case StructTypeS st -> objFactory.tupleType(map(st.fields(), isig -> visit(isig.type())));
      case VariableS v ->  objFactory.variable(v.name());
      case ArrayTypeS a -> objFactory.arrayT(visit(a.elem()));
      case FunctionTypeS f -> objFactory.defFuncT(visit(f.result()), map(f.params(), this::visit));
    };
  }
}
