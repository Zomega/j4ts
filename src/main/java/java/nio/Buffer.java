package java.nio;

public abstract class Buffer {
  public int _capacity;
  public boolean readOnly;
  public int _position;
  public int _limit;
  public int _mark;

  public Buffer(int capacity, boolean readOnly) {
    this._capacity = capacity;
    this.readOnly = readOnly;
    this._position = 0;
    this._limit = capacity;
    this._mark = -1;
  }

  public abstract Object array();

  public int arrayOffset() {
    return 0;
  }

  public int capacity() {
    return _capacity;
  }

  public Buffer clear() {
    _limit = _capacity;
    _position = 0;
    _mark = -1;
    return this;
  }

  public Buffer flip() {
    _limit = _position;
    _position = 0;
    _mark = -1;
    return this;
  }

  public boolean hasArray() {
    return array() != null;
  }

  public boolean hasRemaining() {
    return _position < _limit;
  }

  public boolean isDirect() {
    return false;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public int limit() {
    return _limit;
  }

  public Buffer limit(int newLimit) {
    if (newLimit > _capacity) throw new IllegalArgumentException("limit is bigger than capacity");

    _limit = newLimit;
    _position = Math.min(_position, _limit);
    if (_mark > _limit) _mark = -1;
    return this;
  }

  public Buffer mark() {
    _mark = _position;
    return this;
  }

  public int position() {
    return _position;
  }

  public Buffer position(int newPosition) {
    if (newPosition > _limit) throw new IllegalArgumentException("position is bigger than limit");

    _position = newPosition;
    if (_mark > _position) _mark = -1;
    return this;
  }

  public int remaining() {
    return _limit - _position;
  }

  public Buffer reset() {
    if (_mark == -1) throw new InvalidMarkException();
    _position = _mark;
    return this;
  }

  public Buffer rewind() {
    _position = 0;
    _mark = -1;
    return this;
  }
}
