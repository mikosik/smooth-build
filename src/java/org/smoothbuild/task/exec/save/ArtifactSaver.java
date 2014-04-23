package org.smoothbuild.task.exec.save;

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
import org.smoothbuild.lang.base.SBlob;
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
    builder.put(BLOB_ARRAY, new ArraySaver<SBlob>(smoothFileSystem));
    builder.put(FILE_ARRAY, new FileArraySaver(smoothFileSystem, messages));
    builder.put(NIL, new ArraySaver<SNothing>(smoothFileSystem));

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
      throw new Message(FATAL, "Bug in smooth binary.\nUnknown value type "
          + value.getClass().getName());
    }
  }
}
