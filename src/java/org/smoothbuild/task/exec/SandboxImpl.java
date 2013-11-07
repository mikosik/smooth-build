package org.smoothbuild.task.exec;

import org.smoothbuild.db.value.ValueDb;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.FileBuilder;
import org.smoothbuild.plugin.FileSetBuilder;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringSetBuilder;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.base.Task;

import com.google.common.base.Strings;

public class SandboxImpl implements Sandbox {
  private final FileSystem projectFileSystem;
  private final MessageGroup messageGroup;
  private final ValueDb valueDb;

  public SandboxImpl(FileSystem fileSystem, ValueDb valueDb, Task task) {
    this(fileSystem, valueDb, createMessages(task));
  }

  public SandboxImpl(FileSystem fileSystem, ValueDb valueDb, MessageGroup messageGroup) {
    this.projectFileSystem = fileSystem;
    this.valueDb = valueDb;
    this.messageGroup = messageGroup;
  }

  @Override
  public FileSetBuilder fileSetBuilder() {
    return new FileSetBuilder(valueDb);
  }

  @Override
  public StringSetBuilder stringSetBuilder() {
    return new StringSetBuilder(valueDb);
  }

  @Override
  public FileBuilder fileBuilder() {
    return new FileBuilder(valueDb);
  }

  @Override
  public StringValue string(String string) {
    return valueDb.string(string);
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
