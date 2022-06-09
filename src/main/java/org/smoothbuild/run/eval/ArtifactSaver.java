package org.smoothbuild.run.eval;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Maps.sort;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.lang.define.ObjRefS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.util.collect.DuplicatesDetector;

public class ArtifactSaver {
  private final Reporter reporter;
  private final FileSystem fileSystem;

  @Inject
  public ArtifactSaver(@ForSpace(PRJ) FileSystem fileSystem, Reporter reporter) {
    this.fileSystem = fileSystem;
    this.reporter = reporter;
  }

  public int saveArtifacts(Map<ObjRefS, CnstB> artifacts) {
    reporter.startNewPhase("Saving artifact(s)");
    var sortedPairs = sort(artifacts, comparing(e -> e.getKey().name()));
    for (var pair : sortedPairs.entrySet()) {
      if (!save(pair.getKey(), pair.getValue())) {
        return EXIT_CODE_ERROR;
      }
    }
    return EXIT_CODE_SUCCESS;
  }

  private boolean save(ObjRefS topRef, ObjB obj) {
    String name = topRef.name();
    try {
      PathS path = write(topRef, obj);
      reportSuccess(name, path);
      return true;
    } catch (IOException e) {
      reportError(name,
          "Couldn't store artifact at " + ArtifactPaths.artifactPath(name) + ". Caught exception:\n"
              + getStackTraceAsString(e));
      return false;
    } catch (DuplicatedPathsExc e) {
      reportError(name, e.getMessage());
      return false;
    }
  }

  private PathS write(ObjRefS topRef, ObjB obj) throws IOException, DuplicatedPathsExc {
    PathS artifactPath = ArtifactPaths.artifactPath(topRef.name());
    if (topRef.type() instanceof ArrayTS arrayT) {
      return saveArray(arrayT, artifactPath, (ArrayB) obj);
    } else if (topRef.type().name().equals(FileStruct.NAME)) {
      return saveFile(artifactPath, (TupleB) obj);
    } else {
      return saveBaseObject(artifactPath, obj);
    }
  }

  private PathS saveFile(PathS artifactPath, TupleB file)
      throws IOException, DuplicatedPathsExc {
    saveFileArray(artifactPath, list(file));
    return artifactPath.append(fileObjectPath(file));
  }

  private PathS saveArray(ArrayTS arrayTS, PathS artifactPath,
      ArrayB array) throws IOException, DuplicatedPathsExc {
    fileSystem.createDir(artifactPath);
    TypeS elemTS = arrayTS.elem();
    if (elemTS instanceof ArrayTS elemArrayTS) {
      int i = 0;
      for (ArrayB elem : array.elems(ArrayB.class)) {
        saveArray(elemArrayTS, artifactPath.appendPart(Integer.toString(i)), elem);
        i++;
      }
    } else if (elemTS.name().equals(FileStruct.NAME)) {
      saveFileArray(artifactPath, array.elems(TupleB.class));
    } else {
      saveObjectArray(artifactPath, array);
    }
    return artifactPath;
  }

  private void saveObjectArray(PathS artifactPath, ArrayB array) throws IOException {
    int i = 0;
    for (CnstB cnst : array.elems(CnstB.class)) {
      PathS sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      PathS targetPath = ArtifactPaths.targetPath(cnst);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(PathS artifactPath, Iterable<TupleB> files) throws IOException,
      DuplicatedPathsExc {
    DuplicatesDetector<PathS> duplicatesDetector = new DuplicatesDetector<>();
    for (TupleB file : files) {
      PathS filePath = fileObjectPath(file);
      PathS sourcePath = artifactPath.append(filePath);
      if (!duplicatesDetector.addValue(filePath)) {
        PathS targetPath = ArtifactPaths.targetPath(FileStruct.fileContent(file));
        fileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      fileSystem.delete(artifactPath);
      throw duplicatedPathsMessage(duplicatesDetector.getDuplicateValues());
    }
  }

  private DuplicatedPathsExc duplicatedPathsMessage(Set<PathS> duplicates) {
    String delimiter = "\n  ";
    String list = duplicates.stream()
        .map(PathS::q)
        .collect(joining(delimiter));
    return new DuplicatedPathsExc(
        "Can't store array of Files as it contains files with duplicated paths:"
            + delimiter + list);
  }

  private PathS saveBaseObject(PathS artifactPath, ObjB obj) throws IOException {
    PathS targetPath = ArtifactPaths.targetPath(obj);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static PathS fileObjectPath(TupleB file) {
    return path(FileStruct.filePath(file).toJ());
  }

  private void reportSuccess(String name, PathS path) {
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
