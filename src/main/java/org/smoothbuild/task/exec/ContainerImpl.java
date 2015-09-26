package org.smoothbuild.task.exec;

import static org.smoothbuild.db.objects.ObjectsDb.objectsDb;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.io.util.TempDirectoryManager;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.ValueFactory;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;

public class ContainerImpl implements Container {
  private final FileSystem projectFileSystem;
  private final ObjectsDb objectsDb;
  private final TempDirectoryManager tempDirectoryManager;
  private final LoggedMessages messages;
  private final List<TempDirectory> tempDirectories;

  public ContainerImpl(FileSystem projectFileSystem, ObjectsDb objectsDb,
      TempDirectoryManager tempDirectoryManager) {
    this.projectFileSystem = projectFileSystem;
    this.objectsDb = objectsDb;
    this.tempDirectoryManager = tempDirectoryManager;
    this.messages = new LoggedMessages();
    this.tempDirectories = new ArrayList<>();
  }

  public static ContainerImpl containerImpl() {
    final ObjectsDb objectsDb = objectsDb();
    Provider<TempDirectory> tempDirectoryProvider = new Provider<TempDirectory>() {
      @Override
      public TempDirectory get() {
        return new TempDirectory(objectsDb);
      }
    };
    return new ContainerImpl(new MemoryFileSystem(), objectsDb, new TempDirectoryManager(
        tempDirectoryProvider));
  }

  @Override
  public ValueFactory create() {
    return objectsDb;
  }

  public FileSystem projectFileSystem() {
    return projectFileSystem;
  }

  @Override
  public void log(Message message) {
    messages.log(message);
  }

  public LoggedMessages messages() {
    return messages;
  }

  @Override
  public TempDirectory createTempDirectory() {
    TempDirectory tempDirectory = tempDirectoryManager.createTempDirectory();
    tempDirectories.add(tempDirectory);
    return tempDirectory;
  }

  public void destroy() {
    for (TempDirectory tempDirectory : tempDirectories) {
      tempDirectory.destroy();
    }
  }
}
