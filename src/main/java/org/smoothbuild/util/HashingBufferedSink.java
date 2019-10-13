package org.smoothbuild.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.smoothbuild.db.hashed.Hash;

import com.google.common.hash.HashCode;

import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.HashingSink;
import okio.Okio;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public class HashingBufferedSink implements BufferedSink {
  private final HashingSink hashingSink;
  private final BufferedSink bufferedSink;
  private HashCode hash;

  public HashingBufferedSink(Sink sink) {
    this.hashingSink = Hash.hashingSink(sink);
    this.bufferedSink = Okio.buffer(hashingSink);
  }

  public HashCode hash() {
    if (hash == null) {
      if (isOpen()) {
        throw new IllegalStateException("HashingBufferedSink is not closed.");
      } else {
        hash = HashCode.fromBytes(hashingSink.hash().toByteArray());
      }
    }
    return hash;
  }

  @Override
  public Buffer buffer() {
    return bufferedSink.getBuffer();
  }

  @Override
  public Buffer getBuffer() {
    return bufferedSink.getBuffer();
  }

  @Override
  public BufferedSink write(ByteString byteString) throws IOException {
    return bufferedSink.write(byteString);
  }

  @Override
  public BufferedSink write(byte[] source) throws IOException {
    return bufferedSink.write(source);
  }

  @Override
  public BufferedSink write(byte[] source, int offset, int byteCount) throws IOException {
    return bufferedSink.write(source, offset, byteCount);
  }

  @Override
  public long writeAll(Source source) throws IOException {
    return bufferedSink.writeAll(source);
  }

  @Override
  public BufferedSink write(Source source, long byteCount) throws IOException {
    return bufferedSink.write(source, byteCount);
  }

  @Override
  public BufferedSink writeUtf8(String string) throws IOException {
    return bufferedSink.writeUtf8(string);
  }

  @Override
  public BufferedSink writeUtf8(String string, int beginIndex, int endIndex) throws IOException {
    return bufferedSink.writeUtf8(string, beginIndex, endIndex);
  }

  @Override
  public BufferedSink writeUtf8CodePoint(int codePoint) throws IOException {
    return bufferedSink.writeUtf8CodePoint(codePoint);
  }

  @Override
  public BufferedSink writeString(String string, Charset charset) throws IOException {
    return bufferedSink.writeString(string, charset);
  }

  @Override
  public BufferedSink writeString(String string, int beginIndex, int endIndex,
      Charset charset) throws IOException {
    return bufferedSink.writeString(string, beginIndex, endIndex, charset);
  }

  @Override
  public BufferedSink writeByte(int b) throws IOException {
    return bufferedSink.writeByte(b);
  }

  @Override
  public BufferedSink writeShort(int s) throws IOException {
    return bufferedSink.writeShort(s);
  }

  @Override
  public BufferedSink writeShortLe(int s) throws IOException {
    return bufferedSink.writeShortLe(s);
  }

  @Override
  public BufferedSink writeInt(int i) throws IOException {
    return bufferedSink.writeInt(i);
  }

  @Override
  public BufferedSink writeIntLe(int i) throws IOException {
    return bufferedSink.writeIntLe(i);
  }

  @Override
  public BufferedSink writeLong(long v) throws IOException {
    return bufferedSink.writeLong(v);
  }

  @Override
  public BufferedSink writeLongLe(long v) throws IOException {
    return bufferedSink.writeLongLe(v);
  }

  @Override
  public BufferedSink writeDecimalLong(long v) throws IOException {
    return bufferedSink.writeDecimalLong(v);
  }

  @Override
  public BufferedSink writeHexadecimalUnsignedLong(long v) throws IOException {
    return bufferedSink.writeHexadecimalUnsignedLong(v);
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
  public void close() throws IOException {
    bufferedSink.close();
  }

  @Override
  public BufferedSink emit() throws IOException {
    return bufferedSink.emit();
  }

  @Override
  public BufferedSink emitCompleteSegments() throws IOException {
    return bufferedSink.emitCompleteSegments();
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
