package org.smoothbuild.lang.plugin;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.smoothbuild.io.db.value.ValueDb;
import org.smoothbuild.io.fs.base.Path;

public class FileBuilder {
  private final ValueDb valueDb;

  private Path path;
  private ByteArrayOutputStream outputStream;

  public FileBuilder(ValueDb valueDb) {
    this.valueDb = valueDb;
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

    return valueDb.file(path, outputStream.toByteArray());
  }
}
