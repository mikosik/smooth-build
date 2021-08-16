package org.smoothbuild.exec.java;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.TypeNames;
import org.smoothbuild.lang.base.type.Variable;

public class MapTypeToJType {
  public static Class<? extends Obj> mapTypeToJType(Type type) {
    if (type instanceof ArrayType) {
      return Array.class;
    } else if (type instanceof Variable) {
      return Obj.class;
    } else if (type instanceof StructType) {
      return Tuple.class;
    } else if (type instanceof FunctionType) {
      return Tuple.class;
    } else {
      return switch (type.name()) {
        case TypeNames.BLOB -> Blob.class;
        case TypeNames.BOOL -> Bool.class;
        case TypeNames.NOTHING -> Obj.class;
        case TypeNames.STRING -> Str.class;
        default -> throw new IllegalArgumentException("Unknown type: " + type.q());
      };
    }
  }
}
