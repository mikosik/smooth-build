package org.smoothbuild.object;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.File;

public class FileBuilder {
  private final ObjectDb objectDb;

  private Path path;
  private ByteArrayOutputStream outputStream;

  public FileBuilder(ObjectDb objectDb) {
    this.objectDb = objectDb;
  }

  public void setPath(Path path) {
    checkState(this.path == null, "Path has been already set.");
    this.path = checkNotNull(path);
  }

  public OutputStream openOutputStream() {
    checkState(this.outputStream == null, "Cannot open output stream twice.");
    this.outputStream = new ByteArrayOutputStream();
    return outputStream;
  }

  public File build() {
    checkState(outputStream != null, "No file content available. Create one via openOutputStream()");
    checkState(path != null, "No path set");

    return objectDb.file(path, outputStream.toByteArray());
  }
}
