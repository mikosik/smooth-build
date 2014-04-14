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
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.objects.build.ArrayBuilder;
import org.smoothbuild.db.objects.instance.BlobObject;
import org.smoothbuild.db.objects.instance.FileObject;
import org.smoothbuild.db.objects.instance.StringObject;
import org.smoothbuild.db.objects.read.ReadArray;
import org.smoothbuild.db.objects.read.ReadBlob;
import org.smoothbuild.db.objects.read.ReadFile;
import org.smoothbuild.db.objects.read.ReadNothing;
import org.smoothbuild.db.objects.read.ReadString;
import org.smoothbuild.db.objects.read.ReadValue;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class ObjectsDb {
  private final HashedDb hashedDb;

  private final ReadString readString;
  private final ReadBlob readBlob;
  private final ReadFile readFile;
  private final ReadArray<SString> readStringArray;
  private final ReadArray<SBlob> readBlobArray;
  private final ReadArray<SFile> readFileArray;
  private final ReadNothing readNothing;

  private final ImmutableMap<SType<?>, ReadValue<?>> readersMap;

  @Inject
  public ObjectsDb(@Objects HashedDb hashedDb) {
    this.hashedDb = hashedDb;

    this.readString = new ReadString(hashedDb);
    this.readBlob = new ReadBlob(hashedDb);
    this.readFile = new ReadFile(hashedDb);
    this.readStringArray = new ReadArray<SString>(hashedDb, STRING_ARRAY, readString);
    this.readBlobArray = new ReadArray<SBlob>(hashedDb, BLOB_ARRAY, readBlob);
    this.readFileArray = new ReadArray<SFile>(hashedDb, FILE_ARRAY, readFile);
    this.readNothing = new ReadNothing();

    Builder<SType<?>, ReadValue<?>> builder = ImmutableMap.builder();
    builder.put(STRING, readString);
    builder.put(BLOB, readBlob);
    builder.put(FILE, readFile);
    builder.put(STRING_ARRAY, readStringArray);
    builder.put(BLOB_ARRAY, readBlobArray);
    builder.put(FILE_ARRAY, readFileArray);

    this.readersMap = builder.build();
  }

  // array builders

  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    if (arrayType == FILE_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result =
          (ArrayBuilder<T>) new ArrayBuilder<SFile>(hashedDb, FILE_ARRAY, readFile);
      return result;
    }
    if (arrayType == BLOB_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result =
          (ArrayBuilder<T>) new ArrayBuilder<SBlob>(hashedDb, BLOB_ARRAY, readBlob);
      return result;
    }
    if (arrayType == STRING_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result =
          (ArrayBuilder<T>) new ArrayBuilder<SString>(hashedDb, STRING_ARRAY, readString);
      return result;
    }
    if (arrayType == EMPTY_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result =
          (ArrayBuilder<T>) new ArrayBuilder<SNothing>(hashedDb, EMPTY_ARRAY, readNothing);
      return result;
    }

    throw new IllegalArgumentException("Cannot create ArrayBuilder for array type = " + arrayType);
  }

  // writers

  public SFile writeFile(Path path, SBlob content) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(content.hash());
    marshaller.write(path);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    return new FileObject(path, content, hash);
  }

  public SString writeString(String string) {
    HashCode hash = hashedDb.store(string.getBytes(CHARSET));
    return new StringObject(hashedDb, hash);
  }

  public BlobObject writeBlob(byte[] objectBytes) {
    HashCode hash = hashedDb.store(objectBytes);
    return new BlobObject(hashedDb, hash);
  }

  // readers

  public <T extends SValue> T read(SType<T> typeLiteral, HashCode hash) {
    /*
     * Cast is safe as readersMap is immutable and constructed in proper way.
     */
    @SuppressWarnings("unchecked")
    ReadValue<T> reader = (ReadValue<T>) readersMap.get(typeLiteral);
    if (reader == null) {
      throw new Message(FATAL, "Bug in smooth binary: Unexpected value type " + typeLiteral);
    }
    return reader.read(hash);
  }
}
