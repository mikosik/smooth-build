package org.smoothbuild.task.exec.save;

import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.exec.SmoothExecutorMessages;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ArtifactSaver {
  private final ImmutableMap<SType<?>, Saver<?>> saversMap;

  @Inject
  public ArtifactSaver(@SmoothDir FileSystem smoothFileSystem, SmoothExecutorMessages messages) {
    Builder<SType<?>, Saver<?>> builder = ImmutableMap.builder();

    builder.put(STRING, new StringSaver(smoothFileSystem));
    builder.put(BLOB, new BlobSaver(smoothFileSystem));
    builder.put(FILE, new FileSaver(smoothFileSystem));
    builder.put(STRING_ARRAY, new HashedArraySaver<SString>(smoothFileSystem));
    builder.put(BLOB_ARRAY, new HashedArraySaver<SBlob>(smoothFileSystem));
    builder.put(FILE_ARRAY, new FileArraySaver(smoothFileSystem, messages));

    this.saversMap = builder.build();
  }

  public void save(Name name, SValue value) {
    /*
     * We have to cast to Saver<SValue> to avoid compilation warning. Generic
     * type of object being cast is not actually Saver<SValue> but Saver<Xxx>
     * where Xxx is subtype of SValue. As saversMap is immutable and constructed
     * properly in constructor then cast added by erasure in Saver class will
     * succeed.
     */
    @SuppressWarnings("unchecked")
    Saver<SValue> saver = (Saver<SValue>) saversMap.get(value.type());
    if (saver != null) {
      saver.save(name, value);
    } else {
      throw new ErrorMessageException(new Message(FATAL,
          "Bug in smooth binary.\nUnknown value type " + value.getClass().getName()));
    }
  }
}
