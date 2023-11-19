package org.smoothbuild.vm.bytecode.hashed;

import static org.smoothbuild.common.filesystem.base.AssertPath.newUnknownPathState;
import static org.smoothbuild.vm.bytecode.hashed.HashedDb.dbPathTo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.io.DataWriter;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;

import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.HashingSink;
import okio.Okio;
import okio.Source;
import okio.Timeout;

public class HashingBufferedSink implements BufferedSink {
  private final HashingSink hashingSink;
  private final BufferedSink bufferedSink;
  private final FileSystem fileSystem;
  private final PathS tempPath;
  private Hash hash;

  public HashingBufferedSink(FileSystem fileSystem, PathS tempPath)
      throws IOException {
    this.hashingSink = Hash.hashingSink(fileSystem.sinkWithoutBuffer(tempPath));
    this.bufferedSink = Okio.buffer(hashingSink);
    this.fileSystem = fileSystem;
    this.tempPath = tempPath;
  }

  public Hash hash() {
    if (hash == null) {
      if (isOpen()) {
        throw new IllegalStateException("HashingBufferedSink is not closed.");
      } else {
        hash = new Hash(hashingSink.hash());
      }
    }
    return hash;
  }

  public void write(DataWriter dataWriter) throws HashedDbException {
    Helpers.wrapIOExceptionAsHashedDbException(() -> dataWriter.writeTo(this));
  }

  @Override
  public void close() throws IOException {
    bufferedSink.close();
    moveTempFileToDb();
  }

  private void moveTempFileToDb() throws IOException {
    var path = dbPathTo(hash());
    var pathState = fileSystem.pathState(path);
    switch (pathState) {
      case NOTHING -> fileSystem.move(tempPath, path);
      case FILE -> fileSystem.delete(tempPath);
      case DIR -> throw new IOException(
          "Corrupted HashedDb. Cannot store data at " + path.q() + " as it is a directory.");
      default -> throw newUnknownPathState(pathState);
    }
  }

  @Override
  @Deprecated
  public Buffer buffer() {
    return bufferedSink.getBuffer();
  }

  @Override
  public Buffer getBuffer() {
    return bufferedSink.getBuffer();
  }

  @Override
  public BufferedSink write(ByteString byteString) throws IOException {
    bufferedSink.write(byteString);
    return this;
  }

  @Override
  public BufferedSink write(ByteString byteString, int offset, int byteCount) throws IOException {
    bufferedSink.write(byteString, offset, byteCount);
    return this;
  }

  @Override
  public BufferedSink write(byte[] source) throws IOException {
    bufferedSink.write(source);
    return this;
  }

  @Override
  public BufferedSink write(byte[] source, int offset, int byteCount) throws IOException {
     bufferedSink.write(source, offset, byteCount);
     return this;
  }

  @Override
  public long writeAll(Source source) throws IOException {
    return bufferedSink.writeAll(source);
  }

  @Override
  public BufferedSink write(Source source, long byteCount) throws IOException {
    bufferedSink.write(source, byteCount);
    return this;
  }

  @Override
  public BufferedSink writeUtf8(String string) throws IOException {
    bufferedSink.writeUtf8(string);
    return this;
  }

  @Override
  public BufferedSink writeUtf8(String string, int beginIndex, int endIndex) throws IOException {
    bufferedSink.writeUtf8(string, beginIndex, endIndex);
    return this;
  }

  @Override
  public BufferedSink writeUtf8CodePoint(int codePoint) throws IOException {
    bufferedSink.writeUtf8CodePoint(codePoint);
    return this;
  }

  @Override
  public BufferedSink writeString(String string, Charset charset) throws IOException {
    bufferedSink.writeString(string, charset);
    return this;
  }

  @Override
  public BufferedSink writeString(String string, int beginIndex, int endIndex,
      Charset charset) throws IOException {
    bufferedSink.writeString(string, beginIndex, endIndex, charset);
    return this;
  }

  @Override
  public BufferedSink writeByte(int b) throws IOException {
    bufferedSink.writeByte(b);
    return this;
  }

  @Override
  public BufferedSink writeShort(int s) throws IOException {
    bufferedSink.writeShort(s);
    return this;
  }

  @Override
  public BufferedSink writeShortLe(int s) throws IOException {
    bufferedSink.writeShortLe(s);
    return this;
  }

  @Override
  public BufferedSink writeInt(int i) throws IOException {
    bufferedSink.writeInt(i);
    return this;
  }

  @Override
  public BufferedSink writeIntLe(int i) throws IOException {
     bufferedSink.writeIntLe(i);
     return this;
  }

  @Override
  public BufferedSink writeLong(long v) throws IOException {
    bufferedSink.writeLong(v);
    return this;
  }

  @Override
  public BufferedSink writeLongLe(long v) throws IOException {
    bufferedSink.writeLongLe(v);
    return this;
  }

  @Override
  public BufferedSink writeDecimalLong(long v) throws IOException {
    bufferedSink.writeDecimalLong(v);
    return this;
  }

  @Override
  public BufferedSink writeHexadecimalUnsignedLong(long v) throws IOException {
    bufferedSink.writeHexadecimalUnsignedLong(v);
    return this;
  }

  @Override
  public void write(Buffer source, long byteCount) throws IOException {
    bufferedSink.write(source, byteCount);
  }

  @Override
  public void flush() throws IOException {
    bufferedSink.flush();
  }

  @Override
  public Timeout timeout() {
    return bufferedSink.timeout();
  }

  @Override
  public BufferedSink emit() throws IOException {
    bufferedSink.emit();
    return this;
  }

  @Override
  public BufferedSink emitCompleteSegments() throws IOException {
    bufferedSink.emitCompleteSegments();
    return this;
  }

  @Override
  public OutputStream outputStream() {
    return bufferedSink.outputStream();
  }

  @Override
  public int write(ByteBuffer src) throws IOException {
    return bufferedSink.write(src);
  }

  @Override
  public boolean isOpen() {
    return bufferedSink.isOpen();
  }
}
