package org.smoothbuild.db.objects.marshal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.objects.base.ArrayObject;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class ArrayMarshaller<T extends SValue> implements ObjectMarshaller<SArray<T>> {
  private final HashedDb hashedDb;
  private final SArrayType<T> arrayType;
  private final ObjectMarshaller<T> elementMarshaller;

  public ArrayMarshaller(HashedDb hashedDb, SArrayType<T> arrayType,
      ObjectMarshaller<T> elementMarshaller) {
    this.hashedDb = checkNotNull(hashedDb);
    this.arrayType = checkNotNull(arrayType);
    this.elementMarshaller = checkNotNull(elementMarshaller);
  }

  @Override
  public SArray<T> read(HashCode hash) {
    return new ArrayObject<T>(hash, arrayType, this);
  }

  public Iterator<T> readElements(HashCode hash) {
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    for (HashCode elementHash : readElementHashes(hash)) {
      builder.add(elementMarshaller.read(elementHash));
    }
    return builder.build().iterator();
  }

  private List<HashCode> readElementHashes(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      return unmarshaller.readHashList();
    }
  }

  public SArray<T> write(List<T> elements) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(elements);
    byte[] bytes = marshaller.getBytes();

    HashCode hash = hashedDb.write(bytes);
    return new ArrayObject<T>(hash, arrayType, this);
  }
}
