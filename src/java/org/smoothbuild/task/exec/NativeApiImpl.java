package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.temp.TempDirectory;
import org.smoothbuild.io.temp.TempDirectoryManager;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueBuilders;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;

public class NativeApiImpl implements NativeApi {
  private final FileSystem projectFileSystem;
  private final SValueBuilders valueBuilders;
  private final TempDirectoryManager tempDirectoryManager;
  private final LoggedMessages messages;
  private boolean isResultFromCache;

  @Inject
  public NativeApiImpl(@ProjectDir FileSystem fileSystem, SValueBuilders valueBuilders,
      TempDirectoryManager tempDirectoryManager) {
    this.projectFileSystem = fileSystem;
    this.valueBuilders = valueBuilders;
    this.tempDirectoryManager = tempDirectoryManager;
    this.messages = new LoggedMessages();
    this.isResultFromCache = false;
  }

  @Override
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    return valueBuilders.arrayBuilder(arrayType);
  }

  @Override
  public FileBuilder fileBuilder() {
    return valueBuilders.fileBuilder();
  }

  @Override
  public BlobBuilder blobBuilder() {
    return valueBuilders.blobBuilder();
  }

  @Override
  public SString string(String string) {
    return valueBuilders.string(string);
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

  public void setResultIsFromCache() {
    isResultFromCache = true;
  }

  public boolean isResultFromCache() {
    return isResultFromCache;
  }

  @Override
  public TempDirectory createTempDirectory() {
    return tempDirectoryManager.createTempDirectory();
  }
}
