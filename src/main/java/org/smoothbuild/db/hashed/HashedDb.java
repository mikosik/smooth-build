package org.smoothbuild.db.hashed;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static okio.Okio.buffer;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.io.fs.base.AssertPath.newUnknownPathState;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.util.TempManager;

import okio.BufferedSource;

public class HashedDb {
  private final FileSystem fileSystem;
  private final Path rootPath;
  private final TempManager tempManager;

  public HashedDb(FileSystem fileSystem, Path rootPath, TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.tempManager = tempManager;
  }

  public boolean contains(Hash hash) throws CorruptedHashedDbException {
    Path path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    switch (pathState) {
      case FILE:
        return true;
      case DIR:
        throw new CorruptedHashedDbException(
            "Corrupted HashedDb. " + path + " is a directory not a data file.");
      case NOTHING:
        return false;
      default:
        throw newUnknownPathState(pathState);
    }
  }

  public Hash writeString(String string) throws IOException {
    try (HashingBufferedSink sink = sink()) {
      sink.writeString(string, CHARSET);
      sink.close();
      return sink.hash();
    }
  }

  public String readString(Hash hash) throws IOException, DecodingStringException {
    try (BufferedSource source = source(hash)) {
      CharsetDecoder charsetDecoder = CHARSET.newDecoder();
      charsetDecoder.onMalformedInput(REPORT);
      charsetDecoder.onUnmappableCharacter(REPORT);
      return charsetDecoder.decode(wrap(source.readByteArray())).toString();
    } catch (CharacterCodingException e) {
      throw new DecodingStringException(e.getMessage(), e);
    }
  }

  public Hash writeHashes(Hash... hashes) throws IOException {
    try (HashingBufferedSink sink = sink()) {
      for (Hash hash : hashes) {
        sink.write(hash);
      }
      sink.close();
      return sink.hash();
    }
  }

  public List<Hash> readHashes(Hash hash) throws IOException {
    List<Hash> result = new ArrayList<>();
    try (BufferedSource source = source(hash)) {
      while (!source.exhausted()) {
        result.add(Hash.read(source));
      }
    }
    return result;
  }

  public BufferedSource source(Hash hash) throws IOException {
    Path path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    switch (pathState) {
      case FILE:
        return fileSystem.source(path);
      case DIR:
        throw new CorruptedHashedDbException(
            "Corrupted HashedDb. " + path + " is a directory not a data file.");
      case NOTHING:
        throw new NoSuchDataException(hash);
      default:
        throw newUnknownPathState(pathState);
    }
  }

  public HashingBufferedSink sink() throws IOException {
    return new HashingBufferedSink(fileSystem, tempManager.tempPath(), rootPath);
  }

  private Path toPath(Hash hash) {
    return rootPath.append(Hash.toPath(hash));
  }
}
