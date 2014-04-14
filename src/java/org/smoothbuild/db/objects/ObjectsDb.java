package org.smoothbuild.db.objects;

import static org.smoothbuild.SmoothContants.CHARSET;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.base.StringObject;
import org.smoothbuild.db.objects.marshal.ArrayReader;
import org.smoothbuild.db.objects.marshal.ArrayWriter;
import org.smoothbuild.db.objects.marshal.BlobReader;
import org.smoothbuild.db.objects.marshal.BlobWriter;
import org.smoothbuild.db.objects.marshal.FileReader;
import org.smoothbuild.db.objects.marshal.FileWriter;
import org.smoothbuild.db.objects.marshal.NothingReader;
import org.smoothbuild.db.objects.marshal.ObjectReader;
import org.smoothbuild.db.objects.marshal.StringReader;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.FileBuilder;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueBuilders;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class ObjectsDb implements SValueBuilders {
  private final HashedDb hashedDb;

  private final StringReader stringReader;
  private final BlobReader blobReader;
  private final FileReader fileReader;
  private final ArrayReader<SString> stringArrayReader;
  private final ArrayReader<SBlob> blobArrayReader;
  private final ArrayReader<SFile> fileArrayReader;
  private final NothingReader nothingReader;

  private final ImmutableMap<SType<?>, ObjectReader<?>> readersMap;

  @Inject
  public ObjectsDb(@Objects HashedDb hashedDb) {
    this.hashedDb = hashedDb;

    this.stringReader = new StringReader(hashedDb);
    this.blobReader = new BlobReader(hashedDb);
    this.fileReader = new FileReader(hashedDb);
    this.stringArrayReader = new ArrayReader<SString>(hashedDb, STRING_ARRAY, stringReader);
    this.blobArrayReader = new ArrayReader<SBlob>(hashedDb, BLOB_ARRAY, blobReader);
    this.fileArrayReader = new ArrayReader<SFile>(hashedDb, FILE_ARRAY, fileReader);
    this.nothingReader = new NothingReader();

    Builder<SType<?>, ObjectReader<?>> builder = ImmutableMap.builder();
    builder.put(STRING, stringReader);
    builder.put(BLOB, blobReader);
    builder.put(FILE, fileReader);
    builder.put(STRING_ARRAY, stringArrayReader);
    builder.put(BLOB_ARRAY, blobArrayReader);
    builder.put(FILE_ARRAY, fileArrayReader);

    this.readersMap = builder.build();
  }

  @Override
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    if (arrayType == FILE_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result =
          (ArrayBuilder<T>) new ArrayWriter<SFile>(hashedDb, FILE_ARRAY, fileReader);
      return result;
    }
    if (arrayType == BLOB_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result =
          (ArrayBuilder<T>) new ArrayWriter<SBlob>(hashedDb, BLOB_ARRAY, blobReader);
      return result;
    }
    if (arrayType == STRING_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result =
          (ArrayBuilder<T>) new ArrayWriter<SString>(hashedDb, STRING_ARRAY, stringReader);
      return result;
    }
    if (arrayType == EMPTY_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result =
          (ArrayBuilder<T>) new ArrayWriter<SNothing>(hashedDb, EMPTY_ARRAY, nothingReader);
      return result;
    }

    throw new IllegalArgumentException("Cannot create ArrayBuilder for array type = " + arrayType);
  }

  @Override
  public FileBuilder fileBuilder() {
    return new FileWriter(hashedDb);
  }

  @Override
  public BlobBuilder blobBuilder() {
    return new BlobWriter(hashedDb);
  }

  @Override
  public SString string(String string) {
    HashCode hash = hashedDb.store(string.getBytes(CHARSET));
    return new StringObject(hashedDb, hash);
  }

  // readers

  public <T extends SValue> T read(SType<T> typeLiteral, HashCode hash) {
    /*
     * Cast is safe as readersMap is immutable and constructed in proper way.
     */
    @SuppressWarnings("unchecked")
    ObjectReader<T> reader = (ObjectReader<T>) readersMap.get(typeLiteral);
    if (reader == null) {
      throw new Message(FATAL, "Bug in smooth binary: Unexpected value type " + typeLiteral);
    }
    return reader.read(hash);
  }
}
