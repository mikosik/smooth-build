package org.smoothbuild.task.save;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;

import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.DuplicatesDetector;

public class ArtifactSaver {
  private final FileSystem fileSystem;
  private final TypeSystem typeSystem;
  private final Console console;

  @Inject
  public ArtifactSaver(FileSystem fileSystem, TypeSystem typeSystem, Console console) {
    this.fileSystem = fileSystem;
    this.typeSystem = typeSystem;
    this.console = console;
  }

  public void save(Name name, Value value) {
    if (value instanceof Array) {
      saveArray(name, (Array) value);
    } else if (value.type().equals(typeSystem.file())) {
      saveValue(name, ((Struct) value).get("content"));
    } else {
      saveValue(name, value);
    }
  }

  private void saveArray(Name name, Array array) {
    Type elemType = array.type().elemType();
    if (elemType.equals(typeSystem.file())) {
      saveFileArray(name, array);
    } else {
      saveValueArray(name, array);
    }
  }

  private void saveValueArray(Name name, Array array) {
    Path artifactPath = artifactPath(name);

    fileSystem.delete(artifactPath);

    // Create dir explicitly. When fileArray is empty for loop below won't
    // create empty dir for us.
    fileSystem.createDir(artifactPath);

    int i = 0;
    for (Value value : array.asIterable(Value.class)) {
      Path filePath = path(Integer.valueOf(i).toString());
      Path sourcePath = artifactPath.append(filePath);
      Path targetPath = targetPath(value);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Name name, Array fileArray) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();

    Path artifactPath = artifactPath(name);
    fileSystem.delete(artifactPath);

    // Create dir explicitly. When fileArray is empty for loop below won't
    // create empty dir for us.
    fileSystem.createDir(artifactPath);

    for (Struct file : fileArray.asIterable(Struct.class)) {
      Path sourcePath = artifactPath.append(path(((SString) file.get("path")).value()));
      if (!duplicatesDetector.addValue(((SString) file.get("path")).value())) {
        Path targetPath = targetPath(file.get("content"));
        fileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      Set<String> duplicates = duplicatesDetector.getDuplicateValues();
      console.error(duplicatedPathsMessage(name, duplicates));
    }
  }

  private String duplicatedPathsMessage(Name name, Set<String> duplicates) {
    String separator = "\n  ";
    String list = separator + duplicates.stream().collect(joining(separator));
    return "Can't store result of '" + name + "' as it contains files with duplicated paths:"
        + list;
  }

  private void saveValue(Name name, Value value) {
    Path artifactPath = artifactPath(name);
    Path targetPath = targetPath(value);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
  }
}
