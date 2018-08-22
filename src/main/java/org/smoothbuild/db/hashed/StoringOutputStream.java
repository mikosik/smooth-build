package org.smoothbuild.db.hashed;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StoringOutputStream extends OutputStream {
  private final OutputStream outputStream;
  private final FileStorer fileStorer;

  protected StoringOutputStream(OutputStream outputStream, FileStorer fileStorer) {
    this.outputStream = new BufferedOutputStream(outputStream);
    this.fileStorer = fileStorer;
  }

  @Override
  public void write(int b) {
    write(new byte[] { (byte) b });
  }

  @Override
  public void write(byte b[]) {
    write(b, 0, b.length);
  }

  @Override
  public void write(byte bytes[], int off, int len) {
    try {
      outputStream.write(bytes, off, len);
    } catch (IOException e) {
      rethrowAsHashedDbException(e);
    }
  }

  @Override
  public void close() {
    try {
      outputStream.close();
      fileStorer.store();
    } catch (IOException e) {
      rethrowAsHashedDbException(e);
    }
  }

  private void rethrowAsHashedDbException(Throwable e) {
    throw new HashedDbException("IO error occurred while writing object.", e);
  }
}
