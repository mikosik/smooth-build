package org.smoothbuild.db.objects.marshal;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.Objects;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ReadersFactory {
  private final ImmutableMap<SType<?>, ObjectReader<?>> readersMap;

  @Inject
  public ReadersFactory(@Objects HashedDb db) {
    this.readersMap = createMap(db);
  }

  private static ImmutableMap<SType<?>, ObjectReader<?>> createMap(HashedDb db) {
    Builder<SType<?>, ObjectReader<?>> builder = ImmutableMap.builder();

    builder.put(STRING, new StringReader(db));
    builder.put(BLOB, new BlobReader(db));
    builder.put(FILE, new FileReader(db));
    builder.put(NOTHING, new NothingReader());

    builder.put(STRING_ARRAY, new ArrayReader<SString>(db, STRING_ARRAY, new StringReader(db)));
    builder.put(BLOB_ARRAY, new ArrayReader<SBlob>(db, BLOB_ARRAY, new BlobReader(db)));
    builder.put(FILE_ARRAY, new ArrayReader<SFile>(db, FILE_ARRAY, new FileReader(db)));

    return builder.build();
  }

  public <T extends SValue> ObjectReader<T> getReader(SType<T> typeLiteral) {
    /*
     * Cast is safe as readersMap is immutable and constructed in proper way.
     */
    @SuppressWarnings("unchecked")
    ObjectReader<T> reader = (ObjectReader<T>) readersMap.get(typeLiteral);
    if (reader == null) {
      throw new Message(FATAL, "Bug in smooth binary: Unexpected value type " + typeLiteral);
    }
    return reader;
  }
}
