package org.smoothbuild.task.save;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;

import java.util.Set;

import org.smoothbuild.cli.Console;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.DuplicatesDetector;

public class FileArraySaver implements Saver<Array> {
  private final FileSystem smoothFileSystem;
  private final Console console;

  public FileArraySaver(FileSystem smoothFileSystem, Console console) {
    this.smoothFileSystem = smoothFileSystem;
    this.console = console;
  }

  public void save(Name name, Array fileArray) {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();

    Path artifactPath = artifactPath(name);
    smoothFileSystem.delete(artifactPath);

    // Create dir explicitly. When fileArray is empty for loop below won't
    // create empty dir for us.
    smoothFileSystem.createDir(artifactPath);

    for (Value fileValue : fileArray) {
      SFile file = (SFile) fileValue;
      Path sourcePath = artifactPath.append(path(file.path().value()));
      if (!duplicatesDetector.addValue(file.path().value())) {
        Path targetPath = targetPath(file.content());
        smoothFileSystem.createLink(sourcePath, targetPath);
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
}
