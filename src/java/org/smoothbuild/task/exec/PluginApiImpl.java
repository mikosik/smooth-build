package org.smoothbuild.task.exec;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.type.SArrayType;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.lang.type.SValueBuilders;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;

import com.google.common.base.Strings;

public class PluginApiImpl implements PluginApi {
  private final FileSystem projectFileSystem;
  private final SValueBuilders valueBuilders;
  private final MessageGroup messageGroup;

  public PluginApiImpl(FileSystem fileSystem, SValueBuilders valueBuilders, Task task) {
    this(fileSystem, valueBuilders, createMessages(task));
  }

  public PluginApiImpl(FileSystem fileSystem, SValueBuilders valueBuilders,
      MessageGroup messageGroup) {
    this.projectFileSystem = fileSystem;
    this.valueBuilders = valueBuilders;
    this.messageGroup = messageGroup;
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
    // TODO Smooth StackTrace (list of CodeLocations) should be added here. This
    // will be possible when each Task will have parent field pointing in
    // direction to nearest root node (build run can have more than one
    // task-to-run [soon]).
    messageGroup.report(message);
  }

  public MessageGroup messageGroup() {
    return messageGroup;
  }

  private static MessageGroup createMessages(Task task) {
    String locationString = task.codeLocation().toString();
    int paddedLength = UserConsole.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    return new MessageGroup(name + locationString);
  }
}
