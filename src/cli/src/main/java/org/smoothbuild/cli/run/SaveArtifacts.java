package org.smoothbuild.cli.run;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.cli.layout.Layout.ARTIFACTS;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb.dbPathTo;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Set;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.collect.DuplicatesDetector;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.common.tuple.Tuples;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.compilerfrontend.lang.define.SInstantiate;
import org.smoothbuild.compilerfrontend.lang.define.SReference;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.evaluator.EvaluatedExprs;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public class SaveArtifacts implements Task1<EvaluatedExprs, Tuple0> {
  static final String FILE_STRUCT_NAME = "File";
  private final Filesystem filesystem;

  @Inject
  public SaveArtifacts(Filesystem filesystem) {
    this.filesystem = filesystem;
  }

  @Override
  public Output<Tuple0> execute(EvaluatedExprs evaluatedExprs) {
    var label = label("artifacts", "save");
    try {
      filesystem.createDir(ARTIFACTS);
    } catch (IOException e) {
      return output(label, list(error(e.getMessage())));
    }
    var referenceSs = evaluatedExprs.sExprs().map(this::toReferenceS);
    var artifacts = referenceSs.zip(evaluatedExprs.bValues(), Tuples::tuple);
    var logger = new Logger();
    artifacts
        .sortUsing(comparing(a -> a.element1().referencedName()))
        .forEach(t -> save(t.element1(), t.element2(), logger));
    return output(label, logger.toList());
  }

  private SReference toReferenceS(SExpr expr) {
    return (SReference) ((SInstantiate) expr).sPolymorphic();
  }

  private void save(SReference valueS, BValue value, Logger logger) {
    String name = valueS.referencedName();
    try {
      var path = write(valueS, value);
      logger.info(name + " -> " + path.path().q());
    } catch (IOException | BytecodeException e) {
      logger.fatal("Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
          + getStackTraceAsString(e));
    } catch (DuplicatedPathsException e) {
      logger.error(e.getMessage());
    }
  }

  private FullPath write(SReference sReference, BValue value)
      throws IOException, DuplicatedPathsException, BytecodeException {
    FullPath artifactPath = artifactPath(sReference.referencedName());
    if (sReference.schema().type() instanceof SArrayType sArrayType) {
      return saveArray(sArrayType, artifactPath, (BArray) value);
    } else if (sReference.schema().type().name().equals(FILE_STRUCT_NAME)) {
      return saveFile(artifactPath, (BTuple) value);
    } else {
      return saveBaseValue(artifactPath, value);
    }
  }

  private FullPath saveFile(FullPath artifactPath, BTuple file)
      throws IOException, DuplicatedPathsException, BytecodeException {
    saveFileArray(artifactPath, list(file));
    return artifactPath.append(fileValuePath(file));
  }

  private FullPath saveArray(SArrayType sArrayType, FullPath artifactPath, BArray array)
      throws IOException, DuplicatedPathsException, BytecodeException {
    filesystem.createDir(artifactPath);
    SType elemTS = sArrayType.elem();
    if (elemTS instanceof SArrayType sElemArrayType) {
      int i = 0;
      for (BArray elem : array.elements(BArray.class)) {
        saveArray(sElemArrayType, artifactPath.appendPart(Integer.toString(i)), elem);
        i++;
      }
    } else if (elemTS.name().equals(FILE_STRUCT_NAME)) {
      saveFileArray(artifactPath, array.elements(BTuple.class));
    } else {
      saveNonFileArray(artifactPath, array);
    }
    return artifactPath;
  }

  private void saveNonFileArray(FullPath artifactPath, BArray array)
      throws IOException, BytecodeException {
    int i = 0;
    for (var valueB : array.elements(BValue.class)) {
      FullPath sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      FullPath targetPath = targetPath(valueB);
      filesystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(FullPath artifactPath, Iterable<BTuple> files)
      throws IOException, DuplicatedPathsException, BytecodeException {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    for (BTuple file : files) {
      Path filePath = fileValuePath(file);
      FullPath sourcePath = artifactPath.append(filePath);
      if (!duplicatesDetector.addValue(filePath)) {
        FullPath targetPath = targetPath(fileContent(file));
        filesystem.createDir(sourcePath.parent());
        filesystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      filesystem.delete(artifactPath);
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
    filesystem.delete(artifactPath);
    filesystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileValuePath(BTuple file) throws BytecodeException {
    return path(filePath(file).toJavaString());
  }

  private static FullPath targetPath(BValue value) {
    return Layout.BYTECODE_DB.appendPart(dbPathTo(value.dataHash()).toString());
  }

  private static FullPath artifactPath(String name) {
    return ARTIFACTS.appendPart(name);
  }
}
