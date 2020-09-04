package org.smoothbuild.exec.artifact;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.exec.artifact.ArtifactPaths.artifactPath;
import static org.smoothbuild.exec.artifact.ArtifactPaths.targetPath;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.util.DuplicatesDetector;

/**
 * This class is NOT thread-safe.
 */
public class ArtifactSaver {
  private final FileSystem fileSystem;
  private final ObjectFactory objectFactory;

  @Inject
  public ArtifactSaver(FileSystem fileSystem, ObjectFactory objectFactory) {
    this.fileSystem = fileSystem;
    this.objectFactory = objectFactory;
  }

  public Path save(String name, Obj object) throws IOException, DuplicatedPathsException {
    Path artifactPath = artifactPath(name);
    if (object instanceof Array array) {
      return saveArray(artifactPath, array);
    } else if (object.spec().equals(objectFactory.fileSpec())) {
      return saveFile(artifactPath, (Tuple) object);
    } else {
      return saveBasicObject(artifactPath, object);
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
    } else if (elemSpec.equals(objectFactory.fileSpec())) {
      saveFileArray(artifactPath, array.asIterable(Tuple.class));
    } else {
      saveObjectArray(artifactPath, array);
    }
    return artifactPath;
  }

  private void saveObjectArray(Path artifactPath, Array array) throws IOException {
    int i = 0;
    for (Obj object : array.asIterable(Obj.class)) {
      Path sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      Path targetPath = targetPath(object);
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

  private Path saveBasicObject(Path artifactPath, Obj object) throws IOException {
    Path targetPath = targetPath(object);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileObjectPath(Tuple file) {
    return path(filePath(file).jValue());
  }
}
