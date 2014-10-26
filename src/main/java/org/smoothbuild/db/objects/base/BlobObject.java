package org.smoothbuild.db.objects.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.BLOB;

import java.io.IOException;
import java.io.InputStream;

import org.smoothbuild.db.objects.marshal.BlobMarshaller;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.util.Streams;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingInputStream;

public class BlobObject extends AbstractObject implements Blob {
  private final BlobMarshaller marshaller;

  public BlobObject(HashCode hash, BlobMarshaller marshaller) {
    super(BLOB, hash);
    this.marshaller = checkNotNull(marshaller);
  }

  @Override
  public InputStream openInputStream() {
    return marshaller.openInputStream(hash());
  }

  @Override
  public String toString() {
    return "Blob(" + size() + " bytes)";
  }

  private long size() {
    try (CountingInputStream inputStream = new CountingInputStream(openInputStream())) {
      Streams.copy(inputStream, ByteStreams.nullOutputStream());
      return inputStream.getCount();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
