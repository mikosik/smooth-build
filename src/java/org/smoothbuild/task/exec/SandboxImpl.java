package org.smoothbuild.task.exec;

import static org.smoothbuild.lang.type.Type.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Type.FILE_ARRAY;
import static org.smoothbuild.lang.type.Type.STRING_ARRAY;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.plugin.BlobBuilder;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
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

  public ArrayBuilder<?> arrayBuilder(Type<?> arrayType) {
    if (arrayType == FILE_ARRAY) {
      return fileArrayBuilder();
    }
    if (arrayType == BLOB_ARRAY) {
      return blobArrayBuilder();
    }
    if (arrayType == STRING_ARRAY) {
      return stringArrayBuilder();
    }
    throw new IllegalArgumentException("Cannot create ArrayBuilder for array type = " + arrayType);
  }

  @Override
  public ArrayBuilder<SFile> fileArrayBuilder() {
    return valueDb.fileArrayBuilder();
  }

  @Override
  public ArrayBuilder<SBlob> blobArrayBuilder() {
    return valueDb.blobArrayBuilder();
  }

  @Override
  public ArrayBuilder<SString> stringArrayBuilder() {
    return valueDb.stringArrayBuilder();
  }

  @Override
  public FileBuilder fileBuilder() {
    return new FileBuilder(valueDb);
  }

  @Override
  public BlobBuilder blobBuilder() {
    return new BlobBuilder(valueDb);
  }

  @Override
  public SString string(String string) {
    return valueDb.writeString(string);
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
