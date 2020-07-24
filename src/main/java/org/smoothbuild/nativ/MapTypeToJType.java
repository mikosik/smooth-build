package org.smoothbuild.nativ;

import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.TypeNames;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.Nothing;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.record.base.SString;
import org.smoothbuild.record.base.Tuple;

public class MapTypeToJType {
  public static Class<? extends Record> mapTypeToJType(Type type) {
    if (type.isArray()) {
      return Array.class;
    } else if (type.isGeneric()) {
      return Record.class;
    } else if (type instanceof StructType) {
      return Tuple.class;
    } else {
      return switch (type.name()) {
        case TypeNames.BLOB -> Blob.class;
        case TypeNames.BOOL -> Bool.class;
        case TypeNames.NOTHING -> Nothing.class;
        case TypeNames.STRING -> SString.class;
        default -> throw new IllegalArgumentException("Unknown type: '" + type.name() + "'");
      };
    }
  }
}
