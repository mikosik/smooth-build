package org.smoothbuild.db.objects;

import static org.smoothbuild.SmoothContants.CHARSET;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.base.StringObject;
import org.smoothbuild.db.objects.marshal.ReadersFactory;
import org.smoothbuild.db.objects.marshal.WritersFactory;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.FileBuilder;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueBuilders;

import com.google.common.hash.HashCode;

public class ObjectsDb implements SValueBuilders {
  private final HashedDb hashedDb;
  private final ReadersFactory readersFactory;
  private final WritersFactory writersFactory;

  @Inject
  public ObjectsDb(@Objects HashedDb hashedDb, ReadersFactory readersFactory,
      WritersFactory writersFactory) {
    this.hashedDb = hashedDb;
    this.readersFactory = readersFactory;
    this.writersFactory = writersFactory;
  }

  @Override
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    return writersFactory.arrayWriter(arrayType);
  }

  @Override
  public FileBuilder fileBuilder() {
    return writersFactory.fileWriter();
  }

  @Override
  public BlobBuilder blobBuilder() {
    return writersFactory.blobWriter();
  }

  @Override
  public SString string(String string) {
    HashCode hash = hashedDb.write(string.getBytes(CHARSET));
    return new StringObject(hashedDb, hash);
  }

  public <T extends SValue> T read(SType<T> type, HashCode hash) {
    return readersFactory.getReader(type).read(hash);
  }
}
