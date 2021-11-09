package org.smoothbuild.exec.java;

import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.lang.base.type.api.TypeNames;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.VariableS;

public class MapTypeToJType {
  public static Class<? extends ObjectH> mapTypeToJType(TypeS type) {
    if (type instanceof ArrayTypeS) {
      return ArrayH.class;
    } else if (type instanceof VariableS) {
      return ValueH.class;
    } else if (type instanceof StructTypeS) {
      return TupleH.class;
    } else if (type instanceof FunctionTypeS) {
      return TupleH.class;
    } else {
      return switch (type.name()) {
        case TypeNames.BLOB -> BlobH.class;
        case TypeNames.BOOL -> BoolH.class;
        case TypeNames.INT -> IntH.class;
        case TypeNames.NOTHING -> ValueH.class;
        case TypeNames.STRING -> StringH.class;
        default -> throw new IllegalArgumentException("Unknown type: " + type.q());
      };
    }
  }
}
