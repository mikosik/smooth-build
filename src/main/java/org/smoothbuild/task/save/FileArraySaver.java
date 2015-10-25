package org.smoothbuild.task.save;

import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;

import java.util.Set;

import org.smoothbuild.cli.Console;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.task.exec.ExecutionException;
import org.smoothbuild.util.DuplicatesDetector;

import com.google.common.base.Joiner;

public class FileArraySaver implements Saver<Array<SFile>> {
  private final FileSystem smoothFileSystem;
  private final Console console;

  public FileArraySaver(FileSystem smoothFileSystem, Console console) {
    this.smoothFileSystem = smoothFileSystem;
    this.console = console;
  }

  @Override
  public void save(Name name, Array<SFile> fileArray) {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();

    Path artifactPath = artifactPath(name);
    smoothFileSystem.delete(artifactPath);

    // Create directory explicitly. When fileArray is empty for loop below won't
    // create empty dir for us.
    smoothFileSystem.createDir(artifactPath);

    for (SFile file : fileArray) {
      Path sourcePath = artifactPath.append(file.path());
      if (!duplicatesDetector.addValue(file.path())) {
        Path targetPath = targetPath(file.content());
        smoothFileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      Set<Path> duplicates = duplicatesDetector.getDuplicateValues();
      console.error(duplicatedPathsMessage(name, duplicates));
      throw new ExecutionException();
    }
  }

  private String duplicatedPathsMessage(Name name, Set<Path> duplicates) {
    String separator = "\n  ";
    String list = separator + Joiner.on(separator).join(duplicates);
    return "Can't store result of " + name + " as it contains files with duplicated paths:" + list;
  }
}
