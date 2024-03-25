package org.smoothbuild.app.run.eval;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.app.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.app.layout.Layout.HASHED_DB_PATH;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb.dbPathTo;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Set;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.collect.DuplicatesDetector;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dag.TryFunction1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
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
import org.smoothbuild.virtualmachine.wire.Project;

public class SaveArtifacts implements TryFunction1<EvaluatedExprs, Void> {
  static final String FILE_STRUCT_NAME = "File";
  private final Bucket bucket;

  @Inject
  public SaveArtifacts(@Project Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Label label() {
    return Label.label("artifacts", "save");
  }

  @Override
  public Try<Void> apply(EvaluatedExprs evaluatedExprs) {
    try {
      bucket.createDir(ARTIFACTS_PATH);
    } catch (IOException e) {
      return failure(error(e.getMessage()));
    }
    var referenceSs = evaluatedExprs.sExprs().map(this::toReferenceS);
    var artifacts = referenceSs.zip(evaluatedExprs.bValues(), Tuples::tuple);
    var logger = new Logger();
    artifacts
        .sortUsing(comparing(a -> a.element1().referencedName()))
        .forEach(t -> save(t.element1(), t.element2(), logger));
    return Try.of(null, logger);
  }

  private SReference toReferenceS(SExpr expr) {
    return (SReference) ((SInstantiate) expr).sPolymorphic();
  }

  private Maybe<Void> save(SReference valueS, BValue value, Logger logger) {
    String name = valueS.referencedName();
    try {
      var path = write(valueS, value);
      logger.info(name + " -> " + path.q());
      return null;
    } catch (IOException | BytecodeException e) {
      logger.fatal("Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
          + getStackTraceAsString(e));
      return none();
    } catch (DuplicatedPathsException e) {
      logger.error(e.getMessage());
      return none();
    }
  }

  private Path write(SReference sReference, BValue value)
      throws IOException, DuplicatedPathsException, BytecodeException {
    Path artifactPath = artifactPath(sReference.referencedName());
    if (sReference.schema().type() instanceof SArrayType sArrayType) {
      return saveArray(sArrayType, artifactPath, (BArray) value);
    } else if (sReference.schema().type().name().equals(FILE_STRUCT_NAME)) {
      return saveFile(artifactPath, (BTuple) value);
    } else {
      return saveBaseValue(artifactPath, value);
    }
  }

  private Path saveFile(Path artifactPath, BTuple file)
      throws IOException, DuplicatedPathsException, BytecodeException {
    saveFileArray(artifactPath, list(file));
    return artifactPath.append(fileValuePath(file));
  }

  private Path saveArray(SArrayType sArrayType, Path artifactPath, BArray array)
      throws IOException, DuplicatedPathsException, BytecodeException {
    bucket.createDir(artifactPath);
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

  private void saveNonFileArray(Path artifactPath, BArray array)
      throws IOException, BytecodeException {
    int i = 0;
    for (var valueB : array.elements(BValue.class)) {
      Path sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      Path targetPath = targetPath(valueB);
      bucket.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Path artifactPath, Iterable<BTuple> files)
      throws IOException, DuplicatedPathsException, BytecodeException {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    for (BTuple file : files) {
      Path filePath = fileValuePath(file);
      Path sourcePath = artifactPath.append(filePath);
      if (!duplicatesDetector.addValue(filePath)) {
        Path targetPath = targetPath(fileContent(file));
        bucket.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      bucket.delete(artifactPath);
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

  private Path saveBaseValue(Path artifactPath, BValue value) throws IOException {
    Path targetPath = targetPath(value);
    bucket.delete(artifactPath);
    bucket.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileValuePath(BTuple file) throws BytecodeException {
    return path(filePath(file).toJavaString());
  }

  private static Path targetPath(BValue value) {
    return HASHED_DB_PATH.append(dbPathTo(value.dataHash()));
  }

  private static Path artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
