package org.smoothbuild.exec.task.artifact;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.exec.task.artifact.ArtifactPaths.artifactPath;
import static org.smoothbuild.exec.task.artifact.ArtifactPaths.targetPath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.record.db.FileStruct.fileContent;
import static org.smoothbuild.record.db.FileStruct.filePath;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.db.RecordFactory;
import org.smoothbuild.record.spec.Spec;
import org.smoothbuild.util.DuplicatesDetector;

/**
 * This class is NOT thread-safe.
 */
public class ArtifactSaver {
  private final FileSystem fileSystem;
  private final RecordFactory recordFactory;

  @Inject
  public ArtifactSaver(FileSystem fileSystem, RecordFactory recordFactory) {
    this.fileSystem = fileSystem;
    this.recordFactory = recordFactory;
  }

  public Path save(String name, Record record) throws IOException, DuplicatedPathsException {
    Path artifactPath = artifactPath(name);
    if (record instanceof Array) {
      return saveArray(artifactPath, (Array) record);
    } else if (record.spec().equals(recordFactory.fileSpec())) {
      return saveFile(artifactPath, (Tuple) record);
    } else {
      return saveBasicObject(artifactPath, record);
    }
  }

  private Path saveFile(Path artifactPath, Tuple file) throws IOException, DuplicatedPathsException {
    saveFileArray(artifactPath, List.of(file));
    return artifactPath.append(fileObjectPath(file));
  }

  private Path saveArray(Path artifactPath, Array array) throws IOException, DuplicatedPathsException {
    Spec elemSpec = array.spec().elemSpec();
    fileSystem.createDir(artifactPath);
    if (elemSpec.isArray()) {
      int i = 0;
      for (Array element : array.asIterable(Array.class)) {
        saveArray(artifactPath.appendPart(Integer.toString(i)), element);
        i++;
      }
    } else if (elemSpec.equals(recordFactory.fileSpec())) {
      saveFileArray(artifactPath, array.asIterable(Tuple.class));
    } else {
      saveObjectArray(artifactPath, array);
    }
    return artifactPath;
  }

  private void saveObjectArray(Path artifactPath, Array array) throws IOException {
    int i = 0;
    for (Record record : array.asIterable(Record.class)) {
      Path sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      Path targetPath = targetPath(record);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Path artifactPath, Iterable<Tuple> files) throws IOException,
      DuplicatedPathsException {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    for (Tuple file : files) {
      Path filePath = fileObjectPath(file);
      Path sourcePath = artifactPath.append(filePath);
      if (!duplicatesDetector.addValue(filePath)) {
        Path targetPath = targetPath(fileContent(file));
        fileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      fileSystem.delete(artifactPath);
      throw duplicatedPathsMessage(duplicatesDetector.getDuplicateValues());
    }
  }

  private DuplicatedPathsException duplicatedPathsMessage(Set<Path> duplicates) {
    String delimiter = "\n  ";
    String list = duplicates.stream()
        .map(Path::q)
        .collect(joining(delimiter));
    return new DuplicatedPathsException(
        "Can't store array of Files as it contains files with duplicated paths:"
            + delimiter + list);
  }

  private Path saveBasicObject(Path artifactPath, Record record) throws IOException {
    Path targetPath = targetPath(record);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileObjectPath(Tuple file) {
    return path(filePath(file).jValue());
  }
}
