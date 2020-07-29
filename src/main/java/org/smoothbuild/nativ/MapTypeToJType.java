package org.smoothbuild.nativ;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.Bool;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.TypeNames;

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
        case TypeNames.NOTHING -> Record.class;
        case TypeNames.STRING -> RString.class;
        default -> throw new IllegalArgumentException("Unknown type: '" + type.name() + "'");
      };
    }
  }
}
