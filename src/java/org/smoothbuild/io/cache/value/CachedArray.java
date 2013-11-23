package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Unmarshaller;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class CachedArray<T extends Value> extends AbstractValue implements SArray<T> {
  private final HashedDb hashedDb;
  private final ValueReader<T> valueReader;

  public CachedArray(HashedDb hashedDb, HashCode hash, Type<?> type, ValueReader<T> valueReader) {
    super(type, hash);
    this.valueReader = checkNotNull(valueReader);
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public Iterator<T> iterator() {
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    for (HashCode elemHash : readHashCodeList(hash())) {
      builder.add(valueReader.read(elemHash));
    }
    return builder.build().iterator();
  }

  private List<HashCode> readHashCodeList(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      return unmarshaller.readHashCodeList();
    }
  }
}
