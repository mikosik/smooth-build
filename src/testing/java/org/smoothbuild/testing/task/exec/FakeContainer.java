package org.smoothbuild.testing.task.exec;

import javax.inject.Provider;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.io.util.TempDirectoryManager;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.task.exec.ContainerImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class FakeContainer extends ContainerImpl {
  private final FileSystem fileSystem;
  private final FakeObjectsDb objectsDb;

  public FakeContainer() {
    this(new MemoryFileSystem());
  }

  private FakeContainer(FileSystem fileSystem) {
    this(fileSystem, new FakeObjectsDb(fileSystem));
  }

  public FakeContainer(FileSystem fileSystem, FakeObjectsDb objectsDb) {
    super(fileSystem, objectsDb, createTempDirectoryManager(objectsDb));
    this.fileSystem = fileSystem;
    this.objectsDb = objectsDb;
  }

  public static TempDirectoryManager createTempDirectoryManager(final FakeObjectsDb objectsDb) {
    return new TempDirectoryManager(new Provider<TempDirectory>() {
      @Override
      public TempDirectory get() {
        return new TempDirectory(objectsDb);
      }
    });
  }

  @Override
  public FileSystem projectFileSystem() {
    return fileSystem;
  }

  public FakeObjectsDb objectsDb() {
    return objectsDb;
  }

  public SFile file(Path path) {
    return objectsDb.file(path);
  }

  public SFile file(Path path, String content) {
    return objectsDb.file(path, content);
  }
}
