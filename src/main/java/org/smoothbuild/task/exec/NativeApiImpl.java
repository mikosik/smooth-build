package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.io.util.TempDirectoryManager;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;

public class NativeApiImpl implements NativeApi {
  private final FileSystem projectFileSystem;
  private final ObjectsDb objectsDb;
  private final TempDirectoryManager tempDirectoryManager;
  private final LoggedMessages messages;

  @Inject
  public NativeApiImpl(@ProjectDir FileSystem fileSystem, ObjectsDb objectsDb,
      TempDirectoryManager tempDirectoryManager) {
    this.projectFileSystem = fileSystem;
    this.objectsDb = objectsDb;
    this.tempDirectoryManager = tempDirectoryManager;
    this.messages = new LoggedMessages();
  }

  public LoggedMessages messages() {
    return messages;
  }

  @Override
  public <T extends Value> ArrayBuilder<T> arrayBuilder(Class<T> elementType) {
    return objectsDb.arrayBuilder(elementType);
  }

  @Override
  public SFile file(Path path, Blob content) {
    return objectsDb.file(path, content);
  }

  @Override
  public BlobBuilder blobBuilder() {
    return objectsDb.blobBuilder();
  }

  @Override
  public SString string(String string) {
    return objectsDb.string(string);
  }

  public FileSystem projectFileSystem() {
    return projectFileSystem;
  }

  @Override
  public void log(Message message) {
    messages.log(message);
  }

  public LoggedMessages loggedMessages() {
    return messages;
  }

  @Override
  public TempDirectory createTempDirectory() {
    return tempDirectoryManager.createTempDirectory();
  }
}
