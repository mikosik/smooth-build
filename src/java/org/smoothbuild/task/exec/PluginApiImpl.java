package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.type.SArrayType;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.lang.type.SValueBuilders;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.MessageGroup;

public class PluginApiImpl implements PluginApi {
  private final FileSystem projectFileSystem;
  private final SValueBuilders valueBuilders;
  private final MessageGroup messages;
  private boolean isResultFromCache;

  @Inject
  public PluginApiImpl(@ProjectDir FileSystem fileSystem, SValueBuilders valueBuilders) {
    this.projectFileSystem = fileSystem;
    this.valueBuilders = valueBuilders;
    this.messages = new MessageGroup();
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
  public void report(Message message) {
    messages.report(message);
  }

  public MessageGroup messages() {
    return messages;
  }

  public void setResultIsFromCache() {
    isResultFromCache = true;
  }

  public boolean isResultFromCache() {
    return isResultFromCache;
  }
}
