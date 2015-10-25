package org.smoothbuild.task.save;

import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;

import javax.inject.Inject;

import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.listen.UserConsole;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ArtifactSaver {
  private final ImmutableMap<Type, Saver<?>> saversMap;

  @Inject
  public ArtifactSaver(@SmoothDir FileSystem smoothFileSystem, UserConsole console) {
    Builder<Type, Saver<?>> builder = ImmutableMap.builder();

    builder.put(STRING, new StringSaver(smoothFileSystem));
    builder.put(BLOB, new BlobSaver(smoothFileSystem));
    builder.put(FILE, new FileSaver(smoothFileSystem));
    builder.put(STRING_ARRAY, new ArraySaver<SString>(smoothFileSystem));
    builder.put(BLOB_ARRAY, new ArraySaver<Blob>(smoothFileSystem));
    builder.put(FILE_ARRAY, new FileArraySaver(smoothFileSystem, console));
    builder.put(NIL, new ArraySaver<Nothing>(smoothFileSystem));

    this.saversMap = builder.build();
  }

  public <T extends Value> void save(Name name, T value) {
    Saver<T> saver = (Saver<T>) saversMap.get(value.type());
    saver.save(name, value);
  }
}
