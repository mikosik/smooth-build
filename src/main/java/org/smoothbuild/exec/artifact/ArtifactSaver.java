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

import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.db.object.obj.val.TupleB;
import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.exec.base.FileStruct;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.lang.base.type.api.ArrayT;
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

  public Path save(TopRefS topRef, ObjB obj) throws IOException, DuplicatedPathsExc {
    Path artifactPath = artifactPath(topRef.name());
    if (topRef.type() instanceof ArrayT arrayT) {
      return saveArray(arrayT, artifactPath, (ArrayB) obj);
    } else if (topRef.type().name().equals(FileStruct.NAME)) {
      return saveFile(artifactPath, (TupleB) obj);
    } else {
      return saveBaseObject(artifactPath, obj);
    }
  }

  private Path saveFile(Path artifactPath, TupleB file)
      throws IOException, DuplicatedPathsExc {
    saveFileArray(artifactPath, list(file));
    return artifactPath.append(fileObjectPath(file));
  }

  private Path saveArray(ArrayT arrayT, Path artifactPath,
      ArrayB array) throws IOException, DuplicatedPathsExc {
    fileSystem.createDir(artifactPath);
    Type elemT = arrayT.elem();
    if (elemT instanceof ArrayT elemArrayT) {
      int i = 0;
      for (ArrayB elem : array.elems(ArrayB.class)) {
        saveArray(elemArrayT, artifactPath.appendPart(Integer.toString(i)), elem);
        i++;
      }
    } else if (elemT.name().equals(FileStruct.NAME)) {
      saveFileArray(artifactPath, array.elems(TupleB.class));
    } else {
      saveObjectArray(artifactPath, array);
    }
    return artifactPath;
  }

  private void saveObjectArray(Path artifactPath, ArrayB array) throws IOException {
    int i = 0;
    for (ValB val : array.elems(ValB.class)) {
      Path sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      Path targetPath = targetPath(val);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Path artifactPath, Iterable<TupleB> files) throws IOException,
      DuplicatedPathsExc {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    for (TupleB file : files) {
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

  private Path saveBaseObject(Path artifactPath, ObjB obj) throws IOException {
    Path targetPath = targetPath(obj);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileObjectPath(TupleB file) {
    return path(filePath(file).toJ());
  }
}
