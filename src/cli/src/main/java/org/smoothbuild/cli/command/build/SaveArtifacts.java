package org.smoothbuild.cli.command.build;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb.dbPathTo;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Set;
import org.smoothbuild.cli.Artifacts;
import org.smoothbuild.common.collect.DuplicatesDetector;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.common.tuple.Tuples;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SPolyReference;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.evaluator.EvaluatedExprs;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;

public class SaveArtifacts implements Task1<EvaluatedExprs, Tuple0> {
  static final Fqn FILE_STRUCT_FQN = fqn("File");
  private final FileSystem<FullPath> fileSystem;
  private final FullPath artifactsPath;
  private final FullPath bytecodeDbPath;

  @Inject
  public SaveArtifacts(
      FileSystem<FullPath> fileSystem,
      @Artifacts FullPath artifactsPath,
      @BytecodeDb FullPath bytecodeDbPath) {
    this.fileSystem = fileSystem;
    this.artifactsPath = artifactsPath;
    this.bytecodeDbPath = bytecodeDbPath;
  }

  @Override
  public Output<Tuple0> execute(EvaluatedExprs evaluatedExprs) {
    var label = BuildCommand.LABEL.append(":saveArtifacts");
    try {
      fileSystem.createDir(artifactsPath);
    } catch (IOException e) {
      return output(label, list(error(e.getMessage())));
    }
    var sReferences = evaluatedExprs.sExprs().map(this::toReferenceS);
    var artifacts = sReferences.zip(evaluatedExprs.bValues(), Tuples::tuple);
    var logger = new Logger();
    artifacts
        .sortUsing(comparing(a -> a.element1().referencedId().toString()))
        .forEach(t -> save(t.element1(), t.element2(), logger));
    return output(label, logger.toList());
  }

  private SPolyReference toReferenceS(SExpr expr) {
    return (SPolyReference) ((SInstantiate) expr).sPolyReference();
  }

  private void save(SPolyReference valueS, BValue value, Logger logger) {
    var name = valueS.referencedId();
    try {
      var path = write(valueS, value);
      logger.info(name + " -> " + path.path().q());
    } catch (IOException e) {
      logger.fatal("Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
          + getStackTraceAsString(e));
    } catch (DuplicatedPathsException e) {
      logger.error(e.getMessage());
    }
  }

  private FullPath write(SPolyReference sReference, BValue value)
      throws IOException, DuplicatedPathsException {
    FullPath artifactPath = artifactPath(sReference.referencedId());
    if (sReference.scheme().type() instanceof SArrayType sArrayType) {
      return saveArray(sArrayType, artifactPath, (BArray) value);
    } else if (isFileStructType(sReference.scheme().type())) {
      return saveFile(artifactPath, (BTuple) value);
    } else {
      return saveBaseValue(artifactPath, value);
    }
  }

  private static boolean isFileStructType(SType type) {
    return type instanceof SStructType structType && structType.fqn().equals(FILE_STRUCT_FQN);
  }

  private FullPath saveFile(FullPath artifactPath, BTuple file)
      throws IOException, DuplicatedPathsException {
    saveFileArray(artifactPath, list(file));
    return artifactPath.append(fileValuePath(file));
  }

  private FullPath saveArray(SArrayType sArrayType, FullPath artifactPath, BArray array)
      throws IOException, DuplicatedPathsException {
    fileSystem.createDir(artifactPath);
    SType elemTS = sArrayType.elem();
    if (elemTS instanceof SArrayType sElemArrayType) {
      int i = 0;
      for (BArray elem : array.elements(BArray.class)) {
        saveArray(sElemArrayType, artifactPath.appendPart(Integer.toString(i)), elem);
        i++;
      }
    } else if (isFileStructType(elemTS)) {
      saveFileArray(artifactPath, array.elements(BTuple.class));
    } else {
      saveNonFileArray(artifactPath, array);
    }
    return artifactPath;
  }

  private void saveNonFileArray(FullPath artifactPath, BArray array) throws IOException {
    int i = 0;
    for (var valueB : array.elements(BValue.class)) {
      FullPath sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      FullPath targetPath = targetPath(valueB);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(FullPath artifactPath, Iterable<BTuple> files)
      throws IOException, DuplicatedPathsException {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    for (BTuple file : files) {
      Path filePath = fileValuePath(file);
      FullPath sourcePath = artifactPath.append(filePath);
      if (!duplicatesDetector.addValue(filePath)) {
        FullPath targetPath = targetPath(fileContent(file));
        fileSystem.createDir(sourcePath.parent());
        fileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      fileSystem.deleteRecursively(artifactPath);
      throw duplicatedPathsMessage(duplicatesDetector.getDuplicateValues());
    }
  }

  private DuplicatedPathsException duplicatedPathsMessage(Set<Path> duplicates) {
    String delimiter = "\n  ";
    String list = duplicates.stream().map(Path::q).collect(joining(delimiter));
    return new DuplicatedPathsException(
        "Can't store array of Files as it contains files with duplicated paths:" + delimiter
            + list);
  }

  private FullPath saveBaseValue(FullPath artifactPath, BValue value) throws IOException {
    FullPath targetPath = targetPath(value);
    // It is possible that `smooth build` command contains the same argument (value name)
    // passed twice. We allow that, but we need to delete previous in that case.
    fileSystem.deleteRecursively(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileValuePath(BTuple file) throws BytecodeException {
    return path(filePath(file).toJavaString());
  }

  private FullPath targetPath(BValue value) {
    return bytecodeDbPath.appendPart(dbPathTo(value.dataHash()).toString());
  }

  private FullPath artifactPath(Id id) {
    return artifactsPath.appendPart(id.toString());
  }
}
