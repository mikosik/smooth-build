package org.smoothbuild.run.eval;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Try.failure;
import static org.smoothbuild.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.layout.Layout.HASHED_DB_PATH;
import static org.smoothbuild.layout.SmoothSpace.PROJECT;
import static org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb.dbPathTo;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.Set;
import org.smoothbuild.common.collect.DuplicatesDetector;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.log.Logger;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.compilerfrontend.lang.define.InstantiateS;
import org.smoothbuild.compilerfrontend.lang.define.ReferenceS;
import org.smoothbuild.compilerfrontend.lang.type.ArrayTS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;
import org.smoothbuild.layout.ForSpace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.helper.FileStruct;

public class SaveArtifacts implements TryFunction<List<Tuple2<ExprS, ValueB>>, String> {
  private final FileSystem fileSystem;

  @Inject
  public SaveArtifacts(@ForSpace(PROJECT) FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Try<String> apply(List<Tuple2<ExprS, ValueB>> argument) {
    List<Tuple2<ReferenceS, ValueB>> artifacts = argument.map(t -> t.map1(this::toReferenceS));
    try {
      fileSystem.createDir(ARTIFACTS_PATH);
    } catch (IOException e) {
      return failure(error(e.getMessage()));
    }
    var logger = new Logger();
    var sortedArtifacts = artifacts.sortUsing(comparing(a -> a.element1().name()));
    var savedArtifacts =
        sortedArtifacts.map(t -> t.map2(valueB -> save(t.element1(), valueB, logger)));
    var messages = savedArtifacts
        .map(t -> t.element1().name() + " -> " + t.element2().map(PathS::q).getOr("?"))
        .toString("\n");
    return Try.of(messages, logger);
  }

  private ReferenceS toReferenceS(ExprS e) {
    return (ReferenceS) ((InstantiateS) e).polymorphicS();
  }

  private Maybe<PathS> save(ReferenceS valueS, ValueB valueB, Logger logger) {
    String name = valueS.name();
    try {
      var path = write(valueS, valueB);
      return some(path);
    } catch (IOException | BytecodeException e) {
      logger.fatal("Couldn't store artifact at " + artifactPath(name) + ". Caught exception:\n"
          + getStackTraceAsString(e));
      return none();
    } catch (DuplicatedPathsException e) {
      logger.error(e.getMessage());
      return none();
    }
  }

  private PathS write(ReferenceS referenceS, ValueB valueB)
      throws IOException, DuplicatedPathsException, BytecodeException {
    PathS artifactPath = artifactPath(referenceS.name());
    if (referenceS.schema().type() instanceof ArrayTS arrayTS) {
      return saveArray(arrayTS, artifactPath, (ArrayB) valueB);
    } else if (referenceS.schema().type().name().equals(FileStruct.NAME)) {
      return saveFile(artifactPath, (TupleB) valueB);
    } else {
      return saveBaseValue(artifactPath, valueB);
    }
  }

  private PathS saveFile(PathS artifactPath, TupleB file)
      throws IOException, DuplicatedPathsException, BytecodeException {
    saveFileArray(artifactPath, list(file));
    return artifactPath.append(fileValuePath(file));
  }

  private PathS saveArray(ArrayTS arrayTS, PathS artifactPath, ArrayB arrayB)
      throws IOException, DuplicatedPathsException, BytecodeException {
    fileSystem.createDir(artifactPath);
    TypeS elemTS = arrayTS.elem();
    if (elemTS instanceof ArrayTS elemArrayTS) {
      int i = 0;
      for (ArrayB elem : arrayB.elements(ArrayB.class)) {
        saveArray(elemArrayTS, artifactPath.appendPart(Integer.toString(i)), elem);
        i++;
      }
    } else if (elemTS.name().equals(FileStruct.NAME)) {
      saveFileArray(artifactPath, arrayB.elements(TupleB.class));
    } else {
      saveNonFileArray(artifactPath, arrayB);
    }
    return artifactPath;
  }

  private void saveNonFileArray(PathS artifactPath, ArrayB arrayB)
      throws IOException, BytecodeException {
    int i = 0;
    for (var valueB : arrayB.elements(ValueB.class)) {
      PathS sourcePath = artifactPath.appendPart(Integer.valueOf(i).toString());
      PathS targetPath = targetPath(valueB);
      fileSystem.createLink(sourcePath, targetPath);
      i++;
    }
  }

  private void saveFileArray(PathS artifactPath, Iterable<TupleB> files)
      throws IOException, DuplicatedPathsException, BytecodeException {
    DuplicatesDetector<PathS> duplicatesDetector = new DuplicatesDetector<>();
    for (TupleB file : files) {
      PathS filePath = fileValuePath(file);
      PathS sourcePath = artifactPath.append(filePath);
      if (!duplicatesDetector.addValue(filePath)) {
        PathS targetPath = targetPath(fileContent(file));
        fileSystem.createLink(sourcePath, targetPath);
      }
    }

    if (duplicatesDetector.hasDuplicates()) {
      fileSystem.delete(artifactPath);
      throw duplicatedPathsMessage(duplicatesDetector.getDuplicateValues());
    }
  }

  private DuplicatedPathsException duplicatedPathsMessage(Set<PathS> duplicates) {
    String delimiter = "\n  ";
    String list = duplicates.stream().map(PathS::q).collect(joining(delimiter));
    return new DuplicatedPathsException(
        "Can't store array of Files as it contains files with duplicated paths:" + delimiter
            + list);
  }

  private PathS saveBaseValue(PathS artifactPath, ValueB valueB) throws IOException {
    PathS targetPath = targetPath(valueB);
    fileSystem.delete(artifactPath);
    fileSystem.createLink(artifactPath, targetPath);
    return artifactPath;
  }

  private static PathS fileValuePath(TupleB file) throws BytecodeException {
    return path(filePath(file).toJ());
  }

  private static PathS targetPath(ValueB valueB) {
    return HASHED_DB_PATH.append(dbPathTo(valueB.dataHash()));
  }

  private static PathS artifactPath(String name) {
    return ARTIFACTS_PATH.appendPart(name);
  }
}
