package org.smoothbuild.task.exec;

import static org.smoothbuild.db.values.ValuesDb.valuesDb;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Provider;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.ValueFactory;
import org.smoothbuild.message.base.Message;

public class ContainerImpl implements Container {
  private final FileSystem projectFileSystem;
  private final ValuesDb valuesDb;
  private final Provider<TempDirectory> tempDirectoryProvider;
  private final List<Message> messages;
  private final List<TempDirectory> tempDirectories;

  public ContainerImpl(FileSystem projectFileSystem, ValuesDb valuesDb,
      Provider<TempDirectory> tempDirectoryProvider) {
    this.projectFileSystem = projectFileSystem;
    this.valuesDb = valuesDb;
    this.tempDirectoryProvider = tempDirectoryProvider;
    this.messages = new ArrayList<>();
    this.tempDirectories = new ArrayList<>();
  }

  public static ContainerImpl containerImpl() {
    final ValuesDb valuesDb = valuesDb();
    Provider<TempDirectory> tempDirectoryProvider = new Provider<TempDirectory>() {
      @Override
      public TempDirectory get() {
        return new TempDirectory(valuesDb);
      }
    };
    return new ContainerImpl(new MemoryFileSystem(), valuesDb, tempDirectoryProvider);
  }

  @Override
  public ValueFactory create() {
    return valuesDb;
  }

  public FileSystem projectFileSystem() {
    return projectFileSystem;
  }

  @Override
  public void log(Message message) {
    messages.add(message);
  }

  public List<Message> messages() {
    return messages;
  }

  @Override
  public TempDirectory createTempDirectory() {
    TempDirectory tempDirectory = tempDirectoryProvider.get();
    tempDirectories.add(tempDirectory);
    return tempDirectory;
  }

  public void destroy() {
    for (TempDirectory tempDirectory : tempDirectories) {
      tempDirectory.destroy();
    }
  }
}
