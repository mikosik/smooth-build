package org.smoothbuild.db.objects.instance;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.objects.read.ReadValue;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class ArrayObject<T extends SValue> extends AbstractObject implements SArray<T> {
  private final HashedDb hashedDb;
  private final ReadValue<T> readValue;

  public ArrayObject(HashedDb hashedDb, HashCode hash, SType<?> type, ReadValue<T> valueReader) {
    super(type, hash);
    this.readValue = checkNotNull(valueReader);
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public Iterator<T> iterator() {
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    for (HashCode elemHash : readHashCodeList(hash())) {
      builder.add(readValue.read(elemHash));
    }
    return builder.build().iterator();
  }

  private List<HashCode> readHashCodeList(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      return unmarshaller.readHashCodeList();
    }
  }
}
