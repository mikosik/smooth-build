package org.smoothbuild.exec.run.artifact;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.exec.run.artifact.ArtifactPaths.artifactPath;
import static org.smoothbuild.exec.run.artifact.ArtifactPaths.targetPath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.object.db.FileStruct.fileContent;
import static org.smoothbuild.lang.object.db.FileStruct.filePath;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.ConcreteType;
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

  public Path save(String name, SObject object) throws IOException, DuplicatedPathsException {
    Path artifactPath = artifactPath(name);
    if (object instanceof Array) {
      return saveArray(artifactPath, (Array) object);
    } else if (object.type().equals(objectFactory.fileType())) {
      return saveFile(artifactPath, (Struct) object);
    } else {
      return saveBasicObject(artifactPath, object);
    }
  }

  private Path saveFile(Path artifactPath, Struct file) throws IOException, DuplicatedPathsException {
    saveFileArray(artifactPath, List.of(file));
    return artifactPath.append(fileObjectPath(file));
  }

  private Path saveArray(Path artifactPath, Array array) throws IOException, DuplicatedPathsException {
    ConcreteType elemType = array.type().elemType();
    fileSystem.createDir(artifactPath);
    if (elemType.isArray()) {
      int i = 0;
      for (Array element : array.asIterable(Array.class)) {
        saveArray(artifactPath.appendPart(Integer.toString(i)), element);
        i++;
      }
    } else if (elemType.equals(objectFactory.fileType())) {
      saveFileArray(artifactPath, array.asIterable(Struct.class));
    } else {
      saveObjectArray(artifactPath, array);
    }
    return artifactPath;
  }

  private void saveObjectArray(Path artifactPath, Array array) throws IOException {
    int i = 0;
    for (SObject object : array.asIterable(SObject.class)) {
      Path sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      Path targetPath = targetPath(object);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Path artifactPath, Iterable<Struct> files) throws IOException,
      DuplicatedPathsException {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    for (Struct file : files) {
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

  private Path saveBasicObject(Path artifactPath, SObject object) throws IOException {
    Path targetPath = targetPath(object);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileObjectPath(Struct file) {
    return path(filePath(file).jValue());
  }
}
