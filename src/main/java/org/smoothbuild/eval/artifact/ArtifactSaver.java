package org.smoothbuild.eval.artifact;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.eval.artifact.ArtifactPaths.artifactPath;
import static org.smoothbuild.eval.artifact.ArtifactPaths.targetPath;
import static org.smoothbuild.eval.artifact.FileStruct.fileContent;
import static org.smoothbuild.eval.artifact.FileStruct.filePath;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.space.Space.PRJ;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Maps.sort;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.lang.base.type.api.ArrayT;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.expr.TopRefS;
import org.smoothbuild.util.collect.DuplicatesDetector;

public class ArtifactSaver {
  private final Reporter reporter;
  private final FileSystem fileSystem;

  @Inject
  public ArtifactSaver(@ForSpace(PRJ) FileSystem fileSystem, Reporter reporter) {
    this.fileSystem = fileSystem;
    this.reporter = reporter;
  }

  public int saveArtifacts(Map<TopRefS, ObjB> artifacts) {
    reporter.startNewPhase("Saving artifact(s)");
    var sorted = sort(artifacts, comparing(e -> e.getKey().name()));
    for (var entry : sorted.entrySet()) {
      if (!save(entry.getKey(), entry.getValue())) {
        return EXIT_CODE_ERROR;
      }
    }
    return EXIT_CODE_SUCCESS;
  }

  private boolean save(TopRefS topRef, ObjB obj) {
    String name = topRef.name();
    try {
      Path path = write(topRef, obj);
      reportSuccess(name, path);
      return true;
    } catch (IOException e) {
      reportError(name,
          "Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
              + getStackTraceAsString(e));
      return false;
    } catch (DuplicatedPathsExc e) {
      reportError(name, e.getMessage());
      return false;
    }
  }

  private Path write(TopRefS topRef, ObjB obj) throws IOException, DuplicatedPathsExc {
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

  private void reportSuccess(String name, Path path) {
    report(name, path.q(), list());
  }

  private void reportError(String name, String errorMessage) {
    report(name, "???", list(error(errorMessage)));
  }

  private void report(String name, String pathOrError, List<Log> logs) {
    String header = name + " -> " + pathOrError;
    reporter.report(header, logs);
  }
}
