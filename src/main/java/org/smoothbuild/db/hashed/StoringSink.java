package org.smoothbuild.db.hashed;

import java.io.IOException;

import okio.ForwardingSink;
import okio.Sink;

public class StoringSink extends ForwardingSink {
  private final FileStorer fileStorer;

  protected StoringSink(Sink sink, FileStorer fileStorer) {
    super(sink);
    this.fileStorer = fileStorer;
  }

  @Override
  public void close() throws IOException {
    super.close();
    fileStorer.store();
  }
}
