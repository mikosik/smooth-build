package org.smoothbuild.exec.comp;

import static com.google.common.collect.Streams.stream;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.base.Record;

import com.google.common.collect.ImmutableList;

public record Input(ImmutableList<Record> records, Hash hash) {

  public static Input input(Iterable<? extends Record> records) {
    return new Input(ImmutableList.copyOf(records));
  }

  private Input(ImmutableList<Record> records) {
    this(records, calculateHash(records));
  }

  private static Hash calculateHash(Iterable<? extends Record> records) {
    return Hash.of(stream(records).map(Record::hash).toArray(Hash[]::new));
  }
}
