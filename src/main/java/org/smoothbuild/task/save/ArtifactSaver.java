package org.smoothbuild.task.save;

import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.arrayOf;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ArtifactSaver {
  private final ImmutableMap<Type, Saver<?>> saversMap;

  @Inject
  public ArtifactSaver(FileSystem fileSystem, Console console) {
    Builder<Type, Saver<?>> builder = ImmutableMap.builder();
    builder.put(STRING, new StringSaver(fileSystem));
    builder.put(BLOB, new BlobSaver(fileSystem));
    builder.put(FILE, new FileSaver(fileSystem));
    builder.put(arrayOf(STRING), new ArraySaver(fileSystem));
    builder.put(arrayOf(BLOB), new ArraySaver(fileSystem));
    builder.put(arrayOf(FILE), new FileArraySaver(fileSystem, console));
    builder.put(arrayOf(NOTHING), new ArraySaver(fileSystem));
    this.saversMap = builder.build();
  }

  public <T extends Value> void save(Name name, T value) {
    Saver<T> saver = (Saver<T>) saversMap.get(value.type());
    saver.save(name, value);
  }
}
