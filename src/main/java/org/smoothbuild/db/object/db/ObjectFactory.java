package org.smoothbuild.db.object.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.BlobBuilder;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.BlobSpec;
import org.smoothbuild.db.object.spec.BoolSpec;
import org.smoothbuild.db.object.spec.NothingSpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.StringSpec;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.util.io.DataWriter;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ObjectFactory {
  private final ObjectDb objectDb;
  private final TupleSpec messageSpec;
  private final TupleSpec fileSpec;

  @Inject
  public ObjectFactory(ObjectDb objectDb) {
    this.objectDb = objectDb;
    this.messageSpec = createMessageSpec(objectDb);
    this.fileSpec = createFileSpec(objectDb);
  }

  private static TupleSpec createMessageSpec(ObjectDb objectDb) {
    StringSpec stringSpec = objectDb.stringSpec();
    return objectDb.tupleSpec(list(stringSpec, stringSpec));
  }

  private static TupleSpec createFileSpec(ObjectDb objectDb) {
    return objectDb.tupleSpec(list(objectDb.blobSpec(), objectDb.stringSpec()));
  }

  public ArrayBuilder arrayBuilder(Spec elementSpec) {
    return objectDb.arrayBuilder(elementSpec);
  }

  public Blob blob(DataWriter dataWriter) throws IOException {
    try (BlobBuilder builder = blobBuilder()) {
      dataWriter.writeTo(builder.sink());
      return builder.build();
    }
  }

  public BlobBuilder blobBuilder() {
    return objectDb.blobBuilder();
  }

  public Bool bool(boolean value) {
    return objectDb.bool(value);
  }

  public Tuple file(Str path, Blob content) {
    return objectDb.tuple(fileSpec(), list(content, path));
  }

  public Str string(String string) {
    return objectDb.string(string);
  }

  public Tuple tuple(TupleSpec spec, Iterable<? extends Obj> elements) {
    return objectDb.tuple(spec, elements);
  }

  public ArraySpec arraySpec(Spec elementSpec) {
    return objectDb.arraySpec(elementSpec);
  }

  public BlobSpec blobSpec() {
    return objectDb.blobSpec();
  }

  public BoolSpec boolSpec() {
    return objectDb.boolSpec();
  }

  public TupleSpec fileSpec() {
    return fileSpec;
  }

  public TupleSpec messageSpec() {
    return messageSpec;
  }

  public NothingSpec nothingSpec() {
    return objectDb.nothingSpec();
  }

  public StringSpec stringSpec() {
    return objectDb.stringSpec();
  }

  public TupleSpec tupleSpec(Iterable<? extends Spec> elementSpecs) {
    return objectDb.tupleSpec(elementSpecs);
  }

  public Tuple errorMessage(String text) {
    return message(ERROR.name(), text);
  }

  public Tuple warningMessage(String text) {
    return message(WARNING.name(), text);
  }

  public Tuple infoMessage(String text) {
    return message(INFO.name(), text);
  }

  private Tuple message(String severity, String text) {
    Obj textObject = objectDb.string(text);
    Obj severityObject = objectDb.string(severity);
    return objectDb.tuple(messageSpec(), list(textObject, severityObject));
  }
}
