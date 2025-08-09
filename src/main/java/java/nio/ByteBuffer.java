package java.nio;

import static jsweet.util.Lang.any;
import static jsweet.util.Lang.string;

import def.js.Array;
import def.js.ArrayBuffer;
import def.js.DataView;
import def.js.Int8Array;
import java.util.Objects;
import jsweet.util.Lang;

public class ByteBuffer extends Buffer implements Comparable<ByteBuffer> {
  public final ArrayBuffer _buffer;
  public final Int8Array _array;
  public final DataView _data;
  public ByteOrder _order = ByteOrder.BIG_ENDIAN;

  public ByteBuffer(ArrayBuffer _buffer, boolean readOnly) {
    super((int) _buffer.byteLength, readOnly);
    this._buffer = _buffer;
    this._array = new Int8Array(_buffer);
    this._data = new DataView(_buffer);
  }

  public static ByteBuffer allocate(int capacity) {
    return new ByteBuffer(new ArrayBuffer(capacity), false);
  }

  @Override
  public byte[] array() {
    return any(_array);
  }

  public ByteBuffer asReadOnlyBuffer() {
    ByteBuffer byteBuffer = new ByteBuffer(_buffer, true);
    byteBuffer.limit(limit());
    byteBuffer.position(position());
    byteBuffer._mark = _mark;
    return byteBuffer;
  }

  public ByteBuffer compact() {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    int current = position();
    for (int i = current; i < limit(); ++i) {
      put(i - current, get(i));
    }

    position(limit() - position());
    limit(capacity());
    _mark = -1;

    return this;
  }

  @Override
  public int compareTo(ByteBuffer byteBuffer) {
    return _array.toString().compareTo(byteBuffer.toString());
  }

  public ByteBuffer duplicate() {
    ByteBuffer byteBuffer = new ByteBuffer(_buffer, isReadOnly());
    byteBuffer.limit(limit());
    byteBuffer.position(position());
    byteBuffer._mark = _mark;
    return byteBuffer;
  }

  @Override
  public boolean equals(Object o) {
    return this == o || o instanceof ByteBuffer && Objects.equals(_array, ((ByteBuffer) o)._array);
  }

  public byte get() {
    if (position() == limit()) throw new BufferUnderflowException();

    byte b = any(_array.$get(position()));
    position(position() + 1);
    return b;
  }

  public ByteBuffer get(byte[] dest) {
    return get(dest, 0, dest.length);
  }

  public ByteBuffer get(byte[] dest, int offset, int length) {
    if (remaining() < length) throw new BufferUnderflowException();

    for (int i = offset; i < offset + length; i++) dest[i] = get();

    return this;
  }

  public byte get(int from) {
    if (from < 0 || from > limit()) throw new IndexOutOfBoundsException();

    return any(_array.$get(from));
  }

  public char getChar() {
    if (remaining() < 2) throw new BufferUnderflowException();

    char res =
        string(
                def.js.String.fromCharCode(
                    (int) _data.getInt16(position(), _order == ByteOrder.LITTLE_ENDIAN)))
            .charAt(0); // TODO use charset
    position(position() + 2);
    return res;
  }

  public char getChar(int from) {
    if (from < 0 || from > limit() - 1) throw new IndexOutOfBoundsException();

    return string(
            def.js.String.fromCharCode(
                (int) _data.getInt16(from, _order == ByteOrder.LITTLE_ENDIAN)))
        .charAt(0); // TODO use charset
  }

  public double getDouble() {
    if (remaining() < 8) throw new BufferUnderflowException();

    double res = _data.getFloat64(position(), _order == ByteOrder.LITTLE_ENDIAN);
    position(position() + 8);
    return res;
  }

  public double getDouble(int from) {
    if (from < 0 || from > limit() - 7) throw new IndexOutOfBoundsException();

    return _data.getFloat64(from, _order == ByteOrder.LITTLE_ENDIAN);
  }

  public float getFloat() {
    if (remaining() < 4) throw new BufferUnderflowException();

    float res = any(_data.getFloat32(position(), _order == ByteOrder.LITTLE_ENDIAN));
    position(position() + 4);
    return res;
  }

  public float getFloat(int from) {
    if (from < 0 || from > limit() - 3) throw new IndexOutOfBoundsException();

    return any(_data.getFloat32(from, _order == ByteOrder.LITTLE_ENDIAN));
  }

  public int getInt() {
    if (remaining() < 4) throw new BufferUnderflowException();

    int res = any(_data.getInt32(position(), _order == ByteOrder.LITTLE_ENDIAN));
    position(position() + 4);
    return res;
  }

  public int getInt(int from) {
    if (from < 0 || from > limit() - 3) throw new IndexOutOfBoundsException();

    return any(_data.getInt32(from, _order == ByteOrder.LITTLE_ENDIAN));
  }

  public long getLong() {
    if (remaining() < 8) throw new BufferUnderflowException();

    int res1 = any(_data.getInt32(position(), _order == ByteOrder.LITTLE_ENDIAN));
    int res2 = any(_data.getInt32(position() + 4, _order == ByteOrder.LITTLE_ENDIAN));

    if (_order == ByteOrder.LITTLE_ENDIAN) {
      int tmp = res1;
      res1 = res2;
      res2 = tmp;
    }
    position(position() + 8);

    return res1 * 4294967296L + res2;
  }

  public long getLong(int from) {
    if (from < 0 || from > limit() - 7) throw new IndexOutOfBoundsException();

    int res1 = any(_data.getInt32(from, _order == ByteOrder.LITTLE_ENDIAN));
    int res2 = any(_data.getInt32(from + 4, _order == ByteOrder.LITTLE_ENDIAN));

    if (_order == ByteOrder.LITTLE_ENDIAN) {
      int tmp = res1;
      res1 = res2;
      res2 = tmp;
    }

    return res1 * 4294967296L + res2;
  }

  public short getShort() {
    if (remaining() < 2) throw new BufferUnderflowException();

    short res = any(_data.getInt16(position(), _order == ByteOrder.LITTLE_ENDIAN));
    position(position() + 2);
    return res;
  }

  public short getShort(int from) {
    if (from < 0 || from > limit() - 1) throw new IndexOutOfBoundsException();

    return any(_data.getInt16(from, _order == ByteOrder.LITTLE_ENDIAN));
  }

  public ByteOrder order() {
    return _order;
  }

  public ByteBuffer order(ByteOrder newOrder) {
    _order = newOrder;
    return this;
  }

  public ByteBuffer put(byte b) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (position() == limit()) throw new BufferOverflowException();

    _array.$set(position(), b);
    position(position() + 1);
    return this;
  }

  public ByteBuffer put(byte[] src) {
    return put(src, 0, src.length);
  }

  public ByteBuffer put(byte[] src, int offset, int length) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (remaining() < length) throw new BufferOverflowException();

    for (int i = offset; i < offset + length; i++) put(src[i]);

    return this;
  }

  public ByteBuffer put(int to, byte b) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (to < 0 || to > limit()) throw new IndexOutOfBoundsException();

    _array.$set(to, b);
    return this;
  }

  public ByteBuffer putChar(char value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (remaining() < 2) throw new BufferOverflowException();

    _data.setInt16(
        position(),
        string(value).charCodeAt(0),
        _order == ByteOrder.LITTLE_ENDIAN); // TODO use charset
    position(position() + 2);
    return this;
  }

  public ByteBuffer putChar(int to, char value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (to < 0 || to > limit() - 1) throw new IndexOutOfBoundsException();

    _data.setInt16(
        to, string(value).charCodeAt(0), _order == ByteOrder.LITTLE_ENDIAN); // TODO use charset
    return this;
  }

  public ByteBuffer putDouble(double value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (remaining() < 8) throw new BufferOverflowException();

    _data.setFloat64(position(), value, _order == ByteOrder.LITTLE_ENDIAN);
    position(position() + 8);
    return this;
  }

  public ByteBuffer putDouble(int to, double value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (to < 0 || to > limit() - 7) throw new IndexOutOfBoundsException();

    _data.setFloat64(to, value, _order == ByteOrder.LITTLE_ENDIAN);
    return this;
  }

  public ByteBuffer putFloat(float value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (remaining() < 4) throw new BufferOverflowException();

    _data.setFloat32(position(), value, _order == ByteOrder.LITTLE_ENDIAN);
    position(position() + 4);
    return this;
  }

  public ByteBuffer putFloat(int to, float value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (to < 0 || to > limit() - 3) throw new IndexOutOfBoundsException();

    _data.setFloat32(to, value, _order == ByteOrder.LITTLE_ENDIAN);
    return this;
  }

  public ByteBuffer putInt(int value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (remaining() < 4) throw new BufferOverflowException();

    _data.setInt32(position(), value, _order == ByteOrder.LITTLE_ENDIAN);
    position(position() + 4);
    return this;
  }

  public ByteBuffer putInt(int to, int value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (to < 0 || to > limit() - 3) throw new IndexOutOfBoundsException();

    _data.setInt32(to, value, _order == ByteOrder.LITTLE_ENDIAN);
    return this;
  }

  public ByteBuffer putLong(long value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (remaining() < 8) throw new BufferOverflowException();

    int big = (int) (value / 4294967296L);
    int small = (int) (value % 4294967296L);

    if (_order == ByteOrder.LITTLE_ENDIAN) {
      int tmp = big;
      big = small;
      small = tmp;
    }

    _data.setInt32(position(), big, _order == ByteOrder.LITTLE_ENDIAN);
    _data.setInt32(position() + 4, small, _order == ByteOrder.LITTLE_ENDIAN);
    position(position() + 8);
    return this;
  }

  public ByteBuffer putLong(int to, long value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (to < 0 || to > limit() - 7) throw new IndexOutOfBoundsException();

    int big = (int) (value / 4294967296L);
    int small = (int) (value % 4294967296L);

    if (_order == ByteOrder.LITTLE_ENDIAN) {
      int tmp = big;
      big = small;
      small = tmp;
    }

    _data.setInt32(to, big, _order == ByteOrder.LITTLE_ENDIAN);
    _data.setInt32(to + 4, small, _order == ByteOrder.LITTLE_ENDIAN);
    return this;
  }

  public ByteBuffer putShort(short value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (remaining() < 2) throw new BufferOverflowException();

    _data.setInt16(position(), value, _order == ByteOrder.LITTLE_ENDIAN);
    position(position() + 2);
    return this;
  }

  public ByteBuffer putShort(int to, short value) {
    if (isReadOnly()) throw new ReadOnlyBufferException();

    if (to < 0 || to > limit() - 1) throw new IndexOutOfBoundsException();

    _data.setInt16(to, value, _order == ByteOrder.LITTLE_ENDIAN);
    return this;
  }

  public ByteBuffer slice() {
    return new ByteBuffer(_buffer, isReadOnly());
  }

  @Override
  public int hashCode() {
    return Objects.hash(_array);
  }

  public static ByteBuffer wrap(byte[] array) {
    return wrap(array, 0, array.length);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static ByteBuffer wrap(byte[] array, int offset, int length) {
    ArrayBuffer buffer = new ArrayBuffer(length);
    Int8Array bytes = new Int8Array(buffer);
    bytes.set(Int8Array.from((Array) Lang.array(array).slice(offset, offset + length)));
    return new ByteBuffer(
        buffer, true); // currently can not able to write back. This is why it is a read-only
    // buffer
  }

  public static ByteBuffer wrap(ArrayBuffer array) {
    return wrap(array, 0, array.byteLength);
  }

  private static ByteBuffer wrap(ArrayBuffer array, int offset, double length) {
    return new ByteBuffer(array.slice(offset, offset + length), false);
  }
}
