package org.smoothbuild.task.exec.save;

import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;

import javax.inject.Inject;

import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.listen.ErrorMessageException;

public class ArtifactSaver {
  private final StringSaver stringSaver;
  private final FileSaver fileSaver;
  private final StringArraySaver stringArraySaver;
  private final FileArraySaver fileArraySaver;

  @Inject
  public ArtifactSaver(@SmoothDir FileSystem smoothFileSystem) {
    this.stringSaver = new StringSaver(smoothFileSystem);
    this.fileSaver = new FileSaver(smoothFileSystem);
    this.stringArraySaver = new StringArraySaver(smoothFileSystem);
    this.fileArraySaver = new FileArraySaver(smoothFileSystem);
  }

  public void save(Name name, SValue value) {
    if (value.type() == FILE) {
      fileSaver.save(name, (SFile) value);
    } else if (value.type() == FILE_ARRAY) {
      @SuppressWarnings("unchecked")
      SArray<SFile> fileArray = (SArray<SFile>) value;
      fileArraySaver.save(name, fileArray);
    } else if (value.type() == STRING) {
      stringSaver.save(name, (SString) value);
    } else if (value.type() == STRING_ARRAY) {
      @SuppressWarnings("unchecked")
      SArray<SString> stringArray = (SArray<SString>) value;
      stringArraySaver.save(name, stringArray);
    } else {
      throw new ErrorMessageException(new Message(MessageType.FATAL,
          "Bug in smooth binary.\nUnknown value type " + value.getClass().getName()));
    }
  }
}
