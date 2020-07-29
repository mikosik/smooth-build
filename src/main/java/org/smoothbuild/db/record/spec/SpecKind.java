package org.smoothbuild.db.record.spec;

import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.Bool;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.base.Tuple;

public enum SpecKind {
  SPEC((byte) 0, Spec.class),
  NOTHING((byte) 1, Record.class),
  TUPLE((byte) 2, Tuple.class),
  ARRAY((byte) 3, Array.class),
  BLOB((byte) 4, Blob.class),
  BOOL((byte) 5, Bool.class),
  STRING((byte) 6, RString.class);

  private final byte marker;
  private final Class<? extends Record> jType;

  SpecKind(byte marker, Class<? extends Record> jType) {
    this.marker = marker;
    this.jType = jType;
  }

  public static SpecKind specKindMarkedWith(byte marker) {
    for (SpecKind value : values()) {
      if (value.marker == marker) {
        return value;
      }
    }
    return null;
  }

  public byte marker() {
    return marker;
  }

  public Class<? extends Record> jType() {
    return jType;
  }
}
