package org.smoothbuild.db.objects.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.objects.marshal.ObjectReader;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class ArrayObject<T extends SValue> extends AbstractObject implements SArray<T> {
  private final HashedDb hashedDb;
  private final ObjectReader<T> elementReader;

  public ArrayObject(HashedDb hashedDb, HashCode hash, SType<?> type, ObjectReader<T> elementReader) {
    super(type, hash);
    this.elementReader = checkNotNull(elementReader);
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public Iterator<T> iterator() {
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    for (HashCode elemHash : readHashCodeList(hash())) {
      builder.add(elementReader.read(elemHash));
    }
    return builder.build().iterator();
  }

  private List<HashCode> readHashCodeList(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      return unmarshaller.readHashCodeList();
    }
  }
}
