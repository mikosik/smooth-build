package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.Types.BLOB;

import java.io.IOException;
import java.io.InputStream;

import org.smoothbuild.db.objects.marshal.BlobMarshaller;
import org.smoothbuild.util.Streams;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingInputStream;

/**
 * Smooth Blob. Blob value in smooth language.
 */
public class Blob extends AbstractValue {
  private final BlobMarshaller marshaller;

  public Blob(HashCode hash, BlobMarshaller marshaller) {
    super(BLOB, hash);
    this.marshaller = checkNotNull(marshaller);
  }

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
