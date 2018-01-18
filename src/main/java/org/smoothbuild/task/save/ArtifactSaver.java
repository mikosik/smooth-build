package org.smoothbuild.task.save;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;
import static org.smoothbuild.task.save.ArtifactPaths.toFileName;

import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.DuplicatesDetector;

public class ArtifactSaver {
  private final FileSystem fileSystem;
  private final TypesDb typesDb;
  private final Console console;

  @Inject
  public ArtifactSaver(FileSystem fileSystem, TypesDb typesDb, Console console) {
    this.fileSystem = fileSystem;
    this.typesDb = typesDb;
    this.console = console;
  }

  public void save(Name name, Value value) {
    Path path = path(toFileName(name));
    if (value instanceof Array) {
      saveArray(path, (Array) value);
    } else if (value.type().equals(typesDb.file())) {
      saveBasicValue(path, ((Struct) value).get("content"));
    } else {
      saveBasicValue(path, value);
    }
  }

  private void saveArray(Path path, Array array) {
    Type elemType = array.type().elemType();
    fileSystem.createDir(artifactPath(path));
    if (elemType instanceof ArrayType) {
      int i = 0;
      for (Array element : array.asIterable(Array.class)) {
        saveArray(path.append(path(Integer.toString(i))), element);
        i++;
      }
    } else if (elemType.equals(typesDb.file())) {
      saveFileArray(path, array);
    } else {
      saveValueArray(path, array);
    }
  }

  private void saveValueArray(Path path, Array array) {
    Path artifactPath = artifactPath(path);
    int i = 0;
    for (Value value : array.asIterable(Value.class)) {
      Path filePath = path(Integer.valueOf(i).toString());
      Path sourcePath = artifactPath.append(filePath);
      Path targetPath = targetPath(value);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Path path, Array fileArray) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    Path artifactPath = artifactPath(path);
    for (Struct file : fileArray.asIterable(Struct.class)) {
      Path sourcePath = artifactPath.append(path(((SString) file.get("path")).data()));
      if (!duplicatesDetector.addValue(((SString) file.get("path")).data())) {
        Path targetPath = targetPath(file.get("content"));
        fileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      Set<String> duplicates = duplicatesDetector.getDuplicateValues();
      console.error(duplicatedPathsMessage(path, duplicates));
    }
  }

  private String duplicatedPathsMessage(Path path, Set<String> duplicates) {
    String separator = "\n  ";
    String list = separator + duplicates.stream().collect(joining(separator));
    return "Can't store array of Files as it contains files with duplicated paths:" + list;
  }

  private void saveBasicValue(Path path, Value value) {
    Path artifactPath = artifactPath(path);
    Path targetPath = targetPath(value);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
  }
}
