package org.smoothbuild.exec.java;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeNames;
import org.smoothbuild.lang.base.type.api.Variable;

public class MapTypeToJType {
  public static Class<? extends Obj> mapTypeToJType(Type type) {
    if (type instanceof ArrayType) {
      return Array.class;
    } else if (type instanceof Variable) {
      return Val.class;
    } else if (type instanceof StructType) {
      return Tuple.class;
    } else if (type instanceof FunctionType) {
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
