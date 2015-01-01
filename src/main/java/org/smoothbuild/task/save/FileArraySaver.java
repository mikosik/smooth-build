package org.smoothbuild.task.save;

import static org.smoothbuild.task.save.ArtifactPaths.artifactPath;
import static org.smoothbuild.task.save.ArtifactPaths.targetPath;

import java.util.Set;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.task.save.err.DuplicatePathsInFileArrayArtifactError;
import org.smoothbuild.util.DuplicatesDetector;

public class FileArraySaver implements Saver<Array<SFile>> {
  private final FileSystem smoothFileSystem;
  private final LoggedMessages messages;

  public FileArraySaver(FileSystem smoothFileSystem, LoggedMessages messages) {
    this.smoothFileSystem = smoothFileSystem;
    this.messages = messages;
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
      if (!duplicatesDetector.addValue(sourcePath)) {
        Path targetPath = targetPath(file.content());
        smoothFileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      Set<Path> duplicates = duplicatesDetector.getDuplicateValues();
      messages.log(new DuplicatePathsInFileArrayArtifactError(name, duplicates));
    }
  }
}
