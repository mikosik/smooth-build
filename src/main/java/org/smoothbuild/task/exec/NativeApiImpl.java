package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.io.util.TempDirectoryManager;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueFactory;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;

public class NativeApiImpl implements NativeApi {
  private final FileSystem projectFileSystem;
  private final SValueFactory valueFactory;
  private final TempDirectoryManager tempDirectoryManager;
  private final LoggedMessages messages;

  @Inject
  public NativeApiImpl(@ProjectDir FileSystem fileSystem, SValueFactory valueFactory,
      TempDirectoryManager tempDirectoryManager) {
    this.projectFileSystem = fileSystem;
    this.valueFactory = valueFactory;
    this.tempDirectoryManager = tempDirectoryManager;
    this.messages = new LoggedMessages();
  }

  public LoggedMessages messages() {
    return messages;
  }

  @Override
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    return valueFactory.arrayBuilder(arrayType);
  }

  @Override
  public SFile file(Path path, Blob content) {
    return valueFactory.file(path, content);
  }

  @Override
  public BlobBuilder blobBuilder() {
    return valueFactory.blobBuilder();
  }

  @Override
  public SString string(String string) {
    return valueFactory.string(string);
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
