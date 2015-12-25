package org.smoothbuild.task.exec;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.ValueFactory;

public class ContainerImpl implements Container {
  private final FileSystem projectFileSystem;
  private final ValuesDb valuesDb;
  private final TempManager tempManager;
  private final List<Message> messages;
  private final List<TempDir> tempDirs;

  public ContainerImpl(FileSystem projectFileSystem, ValuesDb valuesDb, TempManager tempManager) {
    this.projectFileSystem = projectFileSystem;
    this.valuesDb = valuesDb;
    this.tempManager = tempManager;
    this.messages = new ArrayList<>();
    this.tempDirs = new ArrayList<>();
  }

  public static ContainerImpl containerImpl() {
    final ValuesDb valuesDb = memoryValuesDb();
    return new ContainerImpl(new MemoryFileSystem(), valuesDb, new TempManager(valuesDb));
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
  public TempDir createTempDir() {
    TempDir tempDir = tempManager.tempDir();
    tempDirs.add(tempDir);
    return tempDir;
  }

  public void destroy() {
    tempDirs.stream().forEach(TempDir::destroy);
  }
}
