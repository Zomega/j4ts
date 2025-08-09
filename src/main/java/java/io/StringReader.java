package java.io;

public class StringReader extends Reader {
  private final char[] charArray;
  private int where;
  private int marked = -1;
  private int markedLen;

  public StringReader(String start) {
    this.charArray = start.toCharArray();
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    if ((off < 0)
        || (off > cbuf.length)
        || (len < 0)
        || ((off + len) > cbuf.length)
        || ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    } else if (len == 0) {
      return 0;
    }
    if (where == charArray.length) return -1;

    int size = Math.min(charArray.length - where, len);

    System.arraycopy(charArray, where, cbuf, off, size);

    where += size;
    return size;
  }

  @Override
  public void close() {
    where = charArray.length;
  }

  @Override
  public boolean ready() {
    return where < charArray.length;
  }

  @Override
  public boolean markSupported() {
    return true;
  }

  @Override
  public void mark(int readAheadLimit) {
    this.marked = where;
    this.markedLen = readAheadLimit;
  }

  @Override
  public void reset() throws IOException {
    if (marked == -1 || where > marked + markedLen) {
      throw new IOException("The stream not been marked or mark has been invalidated");
    }
    this.where = marked;
  }
}
