package org.smoothbuild.exec.artifact;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.exec.artifact.ArtifactPaths.artifactPath;
import static org.smoothbuild.exec.artifact.ArtifactPaths.targetPath;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.space.Space.PRJ;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.exec.base.FileStruct;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.DuplicatesDetector;

/**
 * This class is NOT thread-safe.
 */
public class ArtifactSaver {
  private final FileSystem fileSystem;

  @Inject
  public ArtifactSaver(@ForSpace(PRJ) FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public Path save(Value value, Obj obj) throws IOException, DuplicatedPathsException {
    Path artifactPath = artifactPath(value.name());
    if (value.type() instanceof ArrayType arrayType) {
      return saveArray(arrayType, artifactPath, (Array) obj);
    } else if (value.type().name().equals(FileStruct.NAME)) {
      return saveFile(artifactPath, (Rec) obj);
    } else {
      return saveBaseObject(artifactPath, obj);
    }
  }

  private Path saveFile(Path artifactPath, Rec file) throws IOException, DuplicatedPathsException {
    saveFileArray(artifactPath, list(file));
    return artifactPath.append(fileObjectPath(file));
  }

  private Path saveArray(ArrayType arrayType, Path artifactPath,
      Array array) throws IOException, DuplicatedPathsException {
    fileSystem.createDir(artifactPath);
    Type elemType = arrayType.element();
    if (elemType instanceof ArrayType elemArrayType) {
      int i = 0;
      for (Array element : array.elements(Array.class)) {
        saveArray(elemArrayType, artifactPath.appendPart(Integer.toString(i)), element);
        i++;
      }
    } else if (elemType.name().equals(FileStruct.NAME)) {
      saveFileArray(artifactPath, array.elements(Rec.class));
    } else {
      saveObjectArray(artifactPath, array);
    }
    return artifactPath;
  }

  private void saveObjectArray(Path artifactPath, Array array) throws IOException {
    int i = 0;
    for (Val val : array.elements(Val.class)) {
      Path sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      Path targetPath = targetPath(val);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Path artifactPath, Iterable<Rec> files) throws IOException,
      DuplicatedPathsException {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    for (Rec file : files) {
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

  private Path saveBaseObject(Path artifactPath, Obj object) throws IOException {
    Path targetPath = targetPath(object);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileObjectPath(Rec file) {
    return path(filePath(file).jValue());
  }
}
