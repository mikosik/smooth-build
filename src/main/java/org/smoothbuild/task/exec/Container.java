package org.smoothbuild.task.exec;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.value.ValueFactory;

public class Container implements NativeApi {
  private final FileSystem fileSystem;
  private final ValuesDb valuesDb;
  private final TypeSystem typeSystem;
  private final TempManager tempManager;
  private final List<Message> messages;
  private final List<TempDir> tempDirs;

  @Inject
  public Container(FileSystem fileSystem, ValuesDb valuesDb, TypeSystem typeSystem,
      TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.valuesDb = valuesDb;
    this.typeSystem = typeSystem;
    this.tempManager = tempManager;
    this.messages = new ArrayList<>();
    this.tempDirs = new ArrayList<>();
  }

  public static Container container() {
    MemoryFileSystem fileSystem = new MemoryFileSystem();
    return new Container(
        fileSystem, memoryValuesDb(), new TypeSystem(), new TempManager(fileSystem));
  }

  @Override
  public ValueFactory create() {
    return valuesDb;
  }

  @Override
  public Types types() {
    return typeSystem;
  }

  public FileSystem fileSystem() {
    return fileSystem;
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
    TempDir tempDir = tempManager.tempDir(valuesDb);
    tempDirs.add(tempDir);
    return tempDir;
  }

  public void destroy() {
    tempDirs.stream().forEach(TempDir::destroy);
  }
}
