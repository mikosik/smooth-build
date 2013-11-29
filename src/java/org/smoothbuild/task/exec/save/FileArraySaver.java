package org.smoothbuild.task.exec.save;

import static org.smoothbuild.task.exec.save.Savers.artifactPath;
import static org.smoothbuild.task.exec.save.Savers.targetPath;

import java.util.Set;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.task.exec.save.err.DuplicatePathsInFileArrayArtifactError;
import org.smoothbuild.util.DuplicatesDetector;

public class FileArraySaver implements Saver<SArray<SFile>> {
  private final FileSystem smoothFileSystem;
  private final MessageGroup messages;

  public FileArraySaver(FileSystem smoothFileSystem, MessageGroup messages) {
    this.smoothFileSystem = smoothFileSystem;
    this.messages = messages;
  }

  @Override
  public void save(Name name, SArray<SFile> fileArray) {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<Path>();

    Path artifactPath = artifactPath(name);
    smoothFileSystem.delete(artifactPath);

    // Create directory explicitly. When fileArray is empty for loop below won't
    // create empty dir for us.
    smoothFileSystem.createDir(artifactPath);

    for (SFile file : fileArray) {
      Path sourcePath = artifactPath.append(file.path());
      if (!duplicatesDetector.add(sourcePath)) {
        Path targetPath = targetPath(file.content());
        smoothFileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      Set<Path> duplicates = duplicatesDetector.getDuplicates();
      messages.report(new DuplicatePathsInFileArrayArtifactError(name, duplicates));
    }
  }
}
