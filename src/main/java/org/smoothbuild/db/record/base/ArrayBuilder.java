package org.smoothbuild.db.record.base;

import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.record.db.Helpers.wrapException;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.record.db.RecordDb;
import org.smoothbuild.db.record.spec.ArraySpec;

public class ArrayBuilder {
  private final ArraySpec spec;
  private final RecordDb recordDb;
  private final List<Record> elements;

  public ArrayBuilder(ArraySpec spec, RecordDb recordDb) {
    this.spec = spec;
    this.recordDb = recordDb;
    this.elements = new ArrayList<>();
  }

  public ArrayBuilder addAll(Iterable<? extends Record> records) {
    stream(records).forEach(this::add);
    return this;
  }

  public ArrayBuilder add(Record elem) {
    if (!spec.elemSpec().equals(elem.spec())) {
      throw new IllegalArgumentException("Element spec must be " + spec.elemSpec().name()
          + " but was " + elem.spec().name() + ".");
    }
    Class<?> required = spec.elemSpec().jType();
    if (!required.equals(elem.getClass())) {
      throw new IllegalArgumentException("Element must be instance of java class "
          + required.getCanonicalName() + " but it is instance of "
          + elem.getClass().getCanonicalName() + ".");
    }
    elements.add(elem);
    return this;
  }

  public Array build() {
    return wrapException(() -> recordDb.newArray(spec, elements));
  }
}
