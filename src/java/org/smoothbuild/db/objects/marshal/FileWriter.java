package org.smoothbuild.db.objects.marshal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.objects.base.FileObject;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.FileBuilder;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;

import com.google.common.hash.HashCode;

public class FileWriter implements FileBuilder {
  private final HashedDb hashedDb;

  private Path path;
  private SBlob content;

  public FileWriter(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  @Override
  public FileWriter setPath(Path path) {
    checkState(this.path == null, "Path has been already set.");
    this.path = checkNotNull(path);
    return this;
  }

  @Override
  public FileWriter setContent(SBlob content) {
    checkState(this.content == null, "Content has been already set.");
    this.content = checkNotNull(content);
    return this;
  }

  @Override
  public SFile build() {
    checkState(content != null, "No content set");
    checkState(path != null, "No path set");

    return writeFile(path, content);
  }

  private SFile writeFile(Path path, SBlob content) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(content.hash());
    marshaller.write(path);

    HashCode hash = hashedDb.store(marshaller.getBytes());
    return new FileObject(path, content, hash);
  }
}
