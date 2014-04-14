package org.smoothbuild.db.objects.build;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.FileBuilder;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;

public class FileWriter implements FileBuilder {
  private final ObjectsDb objectsDb;

  private Path path;
  private SBlob content;

  public FileWriter(ObjectsDb objectsDb) {
    this.objectsDb = objectsDb;
  }

  @Override
  public void setPath(Path path) {
    checkState(this.path == null, "Path has been already set.");
    this.path = checkNotNull(path);
  }

  @Override
  public void setContent(SBlob content) {
    checkState(this.content == null, "Content has been already set.");
    this.content = checkNotNull(content);
  }

  @Override
  public SFile build() {
    checkState(content != null, "No content set");
    checkState(path != null, "No path set");

    return objectsDb.writeFile(path, content);
  }
}
