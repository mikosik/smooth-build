package org.smoothbuild.task.save;

import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.FILE_ARRAY;
import static org.smoothbuild.lang.base.Types.NIL;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;

import javax.inject.Inject;

import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.Nothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.task.exec.SmoothExecutorMessages;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ArtifactSaver {
  private final ImmutableMap<Type, Saver<?>> saversMap;

  @Inject
  public ArtifactSaver(@SmoothDir FileSystem smoothFileSystem, SmoothExecutorMessages messages) {
    Builder<Type, Saver<?>> builder = ImmutableMap.builder();

    builder.put(STRING, new StringSaver(smoothFileSystem));
    builder.put(BLOB, new BlobSaver(smoothFileSystem));
    builder.put(FILE, new FileSaver(smoothFileSystem));
    builder.put(STRING_ARRAY, new ArraySaver<SString>(smoothFileSystem));
    builder.put(BLOB_ARRAY, new ArraySaver<Blob>(smoothFileSystem));
    builder.put(FILE_ARRAY, new FileArraySaver(smoothFileSystem, messages));
    builder.put(NIL, new ArraySaver<Nothing>(smoothFileSystem));

    this.saversMap = builder.build();
  }

  public <T extends Value> void save(Name name, T value) {
    Saver<T> saver = (Saver<T>) saversMap.get(value.type());
    saver.save(name, value);
  }
}
