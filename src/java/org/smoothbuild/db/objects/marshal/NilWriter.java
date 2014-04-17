package org.smoothbuild.db.objects.marshal;

import static org.smoothbuild.db.objects.marshal.ArrayWriter.marshalArray;
import static org.smoothbuild.lang.base.STypes.NIL;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.base.ArrayObject;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SNothing;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class NilWriter implements ArrayBuilder<SNothing> {
  private final HashedDb hashedDb;
  private final ObjectReader<SNothing> elementReader;

  public NilWriter(HashedDb hashedDb, ObjectReader<SNothing> elementReader) {
    this.hashedDb = hashedDb;
    this.elementReader = elementReader;
  }

  @Override
  public ArrayBuilder<SNothing> add(SNothing elem) {
    throw new UnsupportedOperationException("Cannot add element to Nil");
  }

  @Override
  public SArray<SNothing> build() {
    HashCode hash = hashedDb.write(marshalArray(ImmutableList.<SNothing> of()));
    return new ArrayObject<SNothing>(hashedDb, hash, NIL, elementReader);
  }
}
