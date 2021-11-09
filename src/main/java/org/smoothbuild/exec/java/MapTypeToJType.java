package org.smoothbuild.exec.java;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.lang.base.type.api.TypeNames;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.VariableS;

public class MapTypeToJType {
  public static Class<? extends Obj> mapTypeToJType(TypeS type) {
    if (type instanceof ArrayTypeS) {
      return Array.class;
    } else if (type instanceof VariableS) {
      return Val.class;
    } else if (type instanceof StructTypeS) {
      return Tuple.class;
    } else if (type instanceof FunctionTypeS) {
      return Tuple.class;
    } else {
      return switch (type.name()) {
        case TypeNames.BLOB -> Blob.class;
        case TypeNames.BOOL -> Bool.class;
        case TypeNames.INT -> Int.class;
        case TypeNames.NOTHING -> Val.class;
        case TypeNames.STRING -> Str.class;
        default -> throw new IllegalArgumentException("Unknown type: " + type.q());
      };
    }
  }
}
