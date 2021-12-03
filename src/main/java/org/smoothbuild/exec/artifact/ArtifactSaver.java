package org.smoothbuild.exec.artifact;

import static java.util.stream.Collectors.joining;
import static org.smoothbuild.exec.artifact.ArtifactPaths.artifactPath;
import static org.smoothbuild.exec.artifact.ArtifactPaths.targetPath;
import static org.smoothbuild.exec.base.FileStruct.fileContent;
import static org.smoothbuild.exec.base.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.space.Space.PRJ;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.exec.base.FileStruct;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.util.collect.DuplicatesDetector;

/**
 * This class is NOT thread-safe.
 */
public class ArtifactSaver {
  private final FileSystem fileSystem;

  @Inject
  public ArtifactSaver(@ForSpace(PRJ) FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public Path save(TopRefS topRef, ObjH obj) throws IOException, DuplicatedPathsExc {
    Path artifactPath = artifactPath(topRef.name());
    if (topRef.type() instanceof ArrayType arrayType) {
      return saveArray(arrayType, artifactPath, (ArrayH) obj);
    } else if (topRef.type().name().equals(FileStruct.NAME)) {
      return saveFile(artifactPath, (TupleH) obj);
    } else {
      return saveBaseObject(artifactPath, obj);
    }
  }

  private Path saveFile(Path artifactPath, TupleH file)
      throws IOException, DuplicatedPathsExc {
    saveFileArray(artifactPath, list(file));
    return artifactPath.append(fileObjectPath(file));
  }

  private Path saveArray(ArrayType arrayType, Path artifactPath,
      ArrayH array) throws IOException, DuplicatedPathsExc {
    fileSystem.createDir(artifactPath);
    Type elemType = arrayType.elem();
    if (elemType instanceof ArrayType elemArrayType) {
      int i = 0;
      for (ArrayH elem : array.elems(ArrayH.class)) {
        saveArray(elemArrayType, artifactPath.appendPart(Integer.toString(i)), elem);
        i++;
      }
    } else if (elemType.name().equals(FileStruct.NAME)) {
      saveFileArray(artifactPath, array.elems(TupleH.class));
    } else {
      saveObjectArray(artifactPath, array);
    }
    return artifactPath;
  }

  private void saveObjectArray(Path artifactPath, ArrayH array) throws IOException {
    int i = 0;
    for (ValH val : array.elems(ValH.class)) {
      Path sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      Path targetPath = targetPath(val);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Path artifactPath, Iterable<TupleH> files) throws IOException,
      DuplicatedPathsExc {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    for (TupleH file : files) {
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

  private DuplicatedPathsExc duplicatedPathsMessage(Set<Path> duplicates) {
    String delimiter = "\n  ";
    String list = duplicates.stream()
        .map(Path::q)
        .collect(joining(delimiter));
    return new DuplicatedPathsExc(
        "Can't store array of Files as it contains files with duplicated paths:"
            + delimiter + list);
  }

  private Path saveBaseObject(Path artifactPath, ObjH obj) throws IOException {
    Path targetPath = targetPath(obj);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileObjectPath(TupleH file) {
    return path(filePath(file).toJ());
  }
}
