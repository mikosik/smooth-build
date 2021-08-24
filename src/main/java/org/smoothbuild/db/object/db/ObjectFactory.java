package org.smoothbuild.db.object.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.base.ArrayBuilder;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.BlobBuilder;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Int;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.BlobSpec;
import org.smoothbuild.db.object.spec.BoolSpec;
import org.smoothbuild.db.object.spec.IntSpec;
import org.smoothbuild.db.object.spec.NothingSpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.StrSpec;
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
    StrSpec strSpec = objectDb.strS();
    return objectDb.tupleS(list(strSpec, strSpec));
  }

  private static TupleSpec createFileSpec(ObjectDb objectDb) {
    return objectDb.tupleS(list(objectDb.blobS(), objectDb.strS()));
  }

  public ArrayBuilder arrayBuilder(Spec elementSpec) {
    return objectDb.arrayBuilder(elementSpec);
  }

  public Blob blob(DataWriter dataWriter) {
    try (BlobBuilder builder = blobBuilder()) {
      builder.write(dataWriter);
      return builder.build();
    } catch (IOException e) {
      throw new ObjectDbException(e);
    }
  }

  public BlobBuilder blobBuilder() {
    return objectDb.blobBuilder();
  }

  public Bool bool(boolean value) {
    return objectDb.boolV(value);
  }

  public Int int_(BigInteger value) {
    return objectDb.intV(value);
  }

  public Tuple file(Str path, Blob content) {
    return objectDb.tupleV(fileSpec(), list(content, path));
  }

  public Str string(String string) {
    return objectDb.strV(string);
  }

  public Tuple tuple(TupleSpec spec, Iterable<? extends Obj> elements) {
    return objectDb.tupleV(spec, elements);
  }

  public ArraySpec arraySpec(Spec elementSpec) {
    return objectDb.arrayS(elementSpec);
  }

  public BlobSpec blobSpec() {
    return objectDb.blobS();
  }

  public BoolSpec boolSpec() {
    return objectDb.boolS();
  }

  public IntSpec intSpec() {
    return objectDb.intS();
  }

  public TupleSpec fileSpec() {
    return fileSpec;
  }

  public TupleSpec messageSpec() {
    return messageSpec;
  }

  public NothingSpec nothingSpec() {
    return objectDb.nothingS();
  }

  public StrSpec stringSpec() {
    return objectDb.strS();
  }

  public TupleSpec tupleSpec(Iterable<? extends Spec> elementSpecs) {
    return objectDb.tupleS(elementSpecs);
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
    Obj textObject = objectDb.strV(text);
    Obj severityObject = objectDb.strV(severity);
    return objectDb.tupleV(messageSpec(), list(textObject, severityObject));
  }
}
