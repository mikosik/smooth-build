package org.smoothbuild.db.record.db;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.record.base.ArrayBuilder;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.base.BlobBuilder;
import org.smoothbuild.db.record.base.Bool;
import org.smoothbuild.db.record.base.Messages;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.db.record.spec.ArraySpec;
import org.smoothbuild.db.record.spec.BlobSpec;
import org.smoothbuild.db.record.spec.BoolSpec;
import org.smoothbuild.db.record.spec.NothingSpec;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.db.record.spec.StringSpec;
import org.smoothbuild.db.record.spec.TupleSpec;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class RecordFactory {
  private final RecordDb recordDb;
  private final TupleSpec messageSpec;
  private final TupleSpec fileSpec;

  @Inject
  public RecordFactory(RecordDb recordDb) {
    this.recordDb = recordDb;
    this.messageSpec = createMessageSpec(recordDb);
    this.fileSpec = createFileSpec(recordDb);
  }

  private static TupleSpec createMessageSpec(RecordDb recordDb) {
    StringSpec stringSpec = recordDb.stringSpec();
    return recordDb.tupleSpec(ImmutableList.of(stringSpec, stringSpec));
  }

  private static TupleSpec createFileSpec(RecordDb recordDb) {
    return recordDb.tupleSpec(ImmutableList.of(recordDb.blobSpec(), recordDb.stringSpec()));
  }

  public ArrayBuilder arrayBuilder(Spec elementSpec) {
    return recordDb.arrayBuilder(elementSpec);
  }

  public Blob blob(DataWriter dataWriter) throws IOException {
    try (BlobBuilder builder = blobBuilder()) {
      dataWriter.writeTo(builder.sink());
      return builder.build();
    }
  }

  public BlobBuilder blobBuilder() {
    return recordDb.blobBuilder();
  }

  public Bool bool(boolean value) {
    return recordDb.bool(value);
  }

  public Tuple file(RString path, Blob content) {
    return recordDb.tuple(fileSpec(), ImmutableList.of(content, path));
  }

  public RString string(String string) {
    return recordDb.string(string);
  }

  public Tuple tuple(TupleSpec spec, Iterable<? extends Record> elements) {
    return recordDb.tuple(spec, elements);
  }

  public ArraySpec arraySpec(Spec elementSpec) {
    return recordDb.arraySpec(elementSpec);
  }

  public BlobSpec blobSpec() {
    return recordDb.blobSpec();
  }

  public BoolSpec boolSpec() {
    return recordDb.boolSpec();
  }

  public TupleSpec fileSpec() {
    return fileSpec;
  }

  public TupleSpec messageSpec() {
    return messageSpec;
  }

  public NothingSpec nothingSpec() {
    return recordDb.nothingSpec();
  }

  public StringSpec stringSpec() {
    return recordDb.stringSpec();
  }

  public TupleSpec tupleSpec(Iterable<? extends Spec> elementSpecs) {
    return recordDb.tupleSpec(elementSpecs);
  }

  public Tuple errorMessage(String text) {
    return message(Messages.ERROR, text);
  }

  public Tuple warningMessage(String text) {
    return message(Messages.WARNING, text);
  }

  public Tuple infoMessage(String text) {
    return message(Messages.INFO, text);
  }

  private Tuple message(String severity, String text) {
    Record textObject = recordDb.string(text);
    Record severityObject = recordDb.string(severity);
    return recordDb.tuple(messageSpec(), ImmutableList.of(textObject, severityObject));
  }
}
