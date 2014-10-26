package org.smoothbuild.task.save;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;
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
    builder.put(STRING_ARRAY, new ArraySaver<SString>(smoothFileSystem));
    builder.put(BLOB_ARRAY, new ArraySaver<Blob>(smoothFileSystem));
    builder.put(FILE_ARRAY, new FileArraySaver(smoothFileSystem, messages));
    builder.put(NIL, new ArraySaver<SNothing>(smoothFileSystem));

    this.saversMap = builder.build();
  }

  public <T extends SValue> void save(Name name, T value) {
    /*
     * Cast is safe as saversMap is constructed in proper way and it is
     * immutable.
     */
    @SuppressWarnings("unchecked")
    Saver<T> saver = (Saver<T>) saversMap.get(value.type());
    if (saver != null) {
      saver.save(name, value);
    } else {
      throw new Message(FATAL, "Unknown value type: " + value.getClass().getName());
    }
  }
}
