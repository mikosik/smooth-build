package org.smoothbuild.exec.run.artifact;

import static java.lang.String.join;
import static org.smoothbuild.exec.run.artifact.ArtifactPaths.artifactPath;
import static org.smoothbuild.exec.run.artifact.ArtifactPaths.targetPath;
import static org.smoothbuild.exec.run.artifact.ArtifactPaths.toFileName;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.TypeNames;
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

  public void save(String name, SObject object) throws IOException, DuplicatedPathsException {
    Path path = path(toFileName(name));
    if (object instanceof Array) {
      saveArray(path, (Array) object);
    } else if (object.type().equals(objectFactory.getType(TypeNames.FILE))) {
      saveBasicObject(path, ((Struct) object).get("content"));
    } else {
      saveBasicObject(path, object);
    }
  }

  private void saveArray(Path path, Array array) throws IOException, DuplicatedPathsException {
    ConcreteType elemType = array.type().elemType();
    fileSystem.createDir(artifactPath(path));
    if (elemType.isArray()) {
      int i = 0;
      for (Array element : array.asIterable(Array.class)) {
        saveArray(path.append(path(Integer.toString(i))), element);
        i++;
      }
    } else if (elemType.equals(objectFactory.getType(TypeNames.FILE))) {
      saveFileArray(path, array);
    } else {
      saveObjectArray(path, array);
    }
  }

  private void saveObjectArray(Path path, Array array) throws IOException {
    Path artifactPath = artifactPath(path);
    int i = 0;
    for (SObject object : array.asIterable(SObject.class)) {
      Path filePath = path(Integer.valueOf(i).toString());
      Path sourcePath = artifactPath.append(filePath);
      Path targetPath = targetPath(object);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Path path, Array fileArray) throws IOException,
      DuplicatedPathsException {
    DuplicatesDetector<String> duplicatesDetector = new DuplicatesDetector<>();
    Path artifactPath = artifactPath(path);
    for (Struct file : fileArray.asIterable(Struct.class)) {
      Path sourcePath = artifactPath.append(path(((SString) file.get("path")).jValue()));
      if (!duplicatesDetector.addValue(((SString) file.get("path")).jValue())) {
        Path targetPath = targetPath(file.get("content"));
        fileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      throw duplicatedPathsMessage(duplicatesDetector.getDuplicateValues());
    }
  }

  private DuplicatedPathsException duplicatedPathsMessage(Set<String> duplicates) {
    String separator = "\n  ";
    String list = separator + join(separator, duplicates);
    return new DuplicatedPathsException(
        "Can't store array of Files as it contains files with duplicated paths:" + list);
  }

  private void saveBasicObject(Path path, SObject object) throws IOException {
    Path artifactPath = artifactPath(path);
    Path targetPath = targetPath(object);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
  }
}
