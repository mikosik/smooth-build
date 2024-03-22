package org.smoothbuild.app.run.eval;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.app.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.app.layout.Layout.HASHED_DB_PATH;
import static org.smoothbuild.app.layout.SmoothBucketId.PROJECT;
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
import org.smoothbuild.app.layout.ForBucket;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.collect.DuplicatesDetector;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dag.TryFunction1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.tuple.Tuples;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.InstantiateS;
import org.smoothbuild.compilerfrontend.lang.define.ReferenceS;
import org.smoothbuild.compilerfrontend.lang.type.ArrayTS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;
import org.smoothbuild.evaluator.EvaluatedExprs;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;

public class SaveArtifacts implements TryFunction1<EvaluatedExprs, String> {
  static final String FILE_STRUCT_NAME = "File";
  private final Bucket bucket;

  @Inject
  public SaveArtifacts(@ForBucket(PROJECT) Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Label label() {
    return Label.label("artifacts", "save");
  }

  @Override
  public Try<String> apply(EvaluatedExprs evaluatedExprs) {
    try {
      bucket.createDir(ARTIFACTS_PATH);
    } catch (IOException e) {
      return failure(error(e.getMessage()));
    }
    var referenceSs = evaluatedExprs.exprSs().map(this::toReferenceS);
    var artifacts = referenceSs.zip(evaluatedExprs.valuesB(), Tuples::tuple);
    var logger = new Logger();
    artifacts
        .sortUsing(comparing(a -> a.element1().referencedName()))
        .forEach(t -> save(t.element1(), t.element2(), logger));
    return Try.of(null, logger);
  }

  private ReferenceS toReferenceS(ExprS expr) {
    return (ReferenceS) ((InstantiateS) expr).polymorphicS();
  }

  private Maybe<Void> save(ReferenceS valueS, ValueB valueB, Logger logger) {
    String name = valueS.referencedName();
    try {
      var path = write(valueS, valueB);
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

  private Path write(ReferenceS referenceS, ValueB valueB)
      throws IOException, DuplicatedPathsException, BytecodeException {
    Path artifactPath = artifactPath(referenceS.referencedName());
    if (referenceS.schema().type() instanceof ArrayTS arrayTS) {
      return saveArray(arrayTS, artifactPath, (ArrayB) valueB);
    } else if (referenceS.schema().type().name().equals(FILE_STRUCT_NAME)) {
      return saveFile(artifactPath, (TupleB) valueB);
    } else {
      return saveBaseValue(artifactPath, valueB);
    }
  }

  private Path saveFile(Path artifactPath, TupleB file)
      throws IOException, DuplicatedPathsException, BytecodeException {
    saveFileArray(artifactPath, list(file));
    return artifactPath.append(fileValuePath(file));
  }

  private Path saveArray(ArrayTS arrayTS, Path artifactPath, ArrayB arrayB)
      throws IOException, DuplicatedPathsException, BytecodeException {
    bucket.createDir(artifactPath);
    TypeS elemTS = arrayTS.elem();
    if (elemTS instanceof ArrayTS elemArrayTS) {
      int i = 0;
      for (ArrayB elem : arrayB.elements(ArrayB.class)) {
        saveArray(elemArrayTS, artifactPath.appendPart(Integer.toString(i)), elem);
        i++;
      }
    } else if (elemTS.name().equals(FILE_STRUCT_NAME)) {
      saveFileArray(artifactPath, arrayB.elements(TupleB.class));
    } else {
      saveNonFileArray(artifactPath, arrayB);
    }
    return artifactPath;
  }

  private void saveNonFileArray(Path artifactPath, ArrayB arrayB)
      throws IOException, BytecodeException {
    int i = 0;
    for (var valueB : arrayB.elements(ValueB.class)) {
      Path sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      Path targetPath = targetPath(valueB);
      bucket.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(Path artifactPath, Iterable<TupleB> files)
      throws IOException, DuplicatedPathsException, BytecodeException {
    DuplicatesDetector<Path> duplicatesDetector = new DuplicatesDetector<>();
    for (TupleB file : files) {
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

  private Path saveBaseValue(Path artifactPath, ValueB valueB) throws IOException {
    Path targetPath = targetPath(valueB);
    bucket.delete(artifactPath);
    bucket.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static Path fileValuePath(TupleB file) throws BytecodeException {
    return path(filePath(file).toJavaString());
  }

  private static Path targetPath(ValueB valueB) {
    return HASHED_DB_PATH.append(dbPathTo(valueB.dataHash()));
  }

  private static Path artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
