package java.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Scanner implements Iterator<String>, Closeable {
  private static final String digit = "[\\d]"; // TODO radix dependent
  private static final String decimalSeparator = "[.,]";
  private static final String numeral = digit + "+";
  private static final String decimalNumeral =
      "(?:"
          + numeral
          + "|"
          + numeral
          + decimalSeparator
          + digit
          + "*|"
          + decimalSeparator
          + digit
          + "+)";
  private static final String exponent = "(?:[eE][+-]?" + digit + "+)";
  private static final String decimal = "(?:[-+]?" + decimalNumeral + exponent + "?)";
  private static final String hexFloat =
      "(?:[-+]?0[xX][0-9a-fA-F]*\\.[0-9a-fA-F]+(?:[pP][-+]?[0-9]+)?)";
  private static final String nonNumber = "(?:NaN|Infinity)";
  private static final String signedNonNumber = "(?:[-+]?" + nonNumber + ")";

  private static final Pattern booleanPattern =
      Pattern.compile("true|TRUE|True|1|false|FALSE|False|0");
  private static final Pattern integerPattern = Pattern.compile("[-+]?" + numeral);
  private static final Pattern floatPattern =
      Pattern.compile(decimal + "|" + hexFloat + "|" + signedNonNumber);

  private static final Pattern endLinePattern = Pattern.compile("[\\n\\r]+");
  private static final Pattern whiteSpacePattern = Pattern.compile("\\s+");

  private final Reader reader;
  private Pattern currentDelimiter = whiteSpacePattern;
  private Matcher matcher;
  private char[] buf = new char[1024];
  private int bufferFilledLength = 0;
  private int currentPosition = 0;
  private int nextTokenStart = 0;

  private int nextDelimiterStart = -1;
  private int nextDelimiterEnd = -1;
  private Pattern nextDelimiterWithPattern;

  private int defaultRadix = 10;

  private boolean closed = false;

  public Scanner(String string) {
    this(new StringReader(string));
  }

  public Scanner(InputStream inputStream) {
    this(new InputStreamReader(inputStream));
  }

  private Scanner(Reader reader) {
    this.reader = reader;
  }

  @Override
  public void close() throws IOException {
    closed = true;
    reader.close();
  }

  public Pattern delimiter() {
    return currentDelimiter;
  }

  public Scanner useDelimiter(String currentDelimiter) {
    return useDelimiter(Pattern.compile(currentDelimiter));
  }

  public Scanner useDelimiter(Pattern currentDelimiter) {
    this.currentDelimiter = currentDelimiter;
    return this;
  }

  @Override
  public boolean hasNext() {
    if (closed && currentPosition == bufferFilledLength) return false;

    if (nextDelimiterStart == -1 || nextDelimiterWithPattern != currentDelimiter) {
      searchNextTo(currentDelimiter);
      nextDelimiterWithPattern = currentDelimiter;
    }

    return currentPosition != bufferFilledLength;
  }

  private void searchNextTo(Pattern pattern) {
    searchNextTo(pattern, false);
  }

  private void searchNextTo(Pattern pattern, boolean canBeEmpty) {
    try {
      nextTokenStart = 0;
      while (!closed || bufferFilledLength != currentPosition + nextTokenStart) {
        matcher =
            pattern.matcher(
                new String(
                    buf,
                    currentPosition + nextTokenStart,
                    bufferFilledLength - currentPosition - nextTokenStart));
        if (matcher.find()) {
          if (matcher.start() > 0 || canBeEmpty) {
            nextDelimiterStart = currentPosition + nextTokenStart + matcher.start();
            nextDelimiterEnd = currentPosition + nextTokenStart + matcher.end();
            return;
          }
          // empty match, move to start the matcher
          nextTokenStart += matcher.end();
          if (currentPosition + nextTokenStart
              < bufferFilledLength) // if we are in the buffer, then try again
          continue;
        }

        if (buf.length == bufferFilledLength) {
          if (currentPosition < buf.length / 2) {
            char[] chars = new char[buf.length * 2];
            System.arraycopy(buf, currentPosition, chars, 0, bufferFilledLength - currentPosition);
            buf = chars;
          } else {
            System.arraycopy(buf, currentPosition, buf, 0, bufferFilledLength - currentPosition);
          }
          bufferFilledLength -= currentPosition;
          currentPosition = 0;
          nextDelimiterStart = nextDelimiterEnd = -1;
        }

        if (closed) break;

        int read = reader.read(buf, bufferFilledLength, buf.length - bufferFilledLength);
        if (read <= 0) {
          try {
            close();
          } catch (IOException ignored) {
          }
        } else {
          bufferFilledLength += read;
        }
      }
    } catch (IOException ignored) {
      try {
        close();
      } catch (IOException ignored2) {
      }
    }
    nextDelimiterStart = nextDelimiterEnd = bufferFilledLength;
  }

  @Override
  public String next() {
    if (!hasNext()) throw new NoSuchElementException("No more token");

    String result =
        new String(
            buf,
            currentPosition + nextTokenStart,
            nextDelimiterStart - currentPosition - nextTokenStart);
    currentPosition = nextDelimiterStart;
    nextDelimiterStart = nextDelimiterEnd = -1;
    return result;
  }

  public boolean hasNext(Pattern pattern) {
    return hasNext()
        && pattern
            .matcher(
                new String(
                    buf,
                    currentPosition + nextTokenStart,
                    nextDelimiterStart - currentPosition - nextTokenStart))
            .matches();
  }

  public boolean hasNext(String pattern) {
    return hasNext()
        && Pattern.matches(
            pattern,
            new String(
                buf,
                currentPosition + nextTokenStart,
                nextDelimiterStart - currentPosition - nextTokenStart));
  }

  public int radix() {
    return defaultRadix;
  }

  public boolean hasNextBoolean() {
    return hasNext(booleanPattern);
  }

  public boolean nextBoolean() {
    if (!hasNextBoolean()) throw new InputMismatchException("Next token is not a boolean");

    char firstChar = next().charAt(0);
    return firstChar == 't' || firstChar == 'T' || firstChar == '1';
  }

  public boolean hasNextByte() {
    return hasNext(integerPattern);
  }

  public byte nextByte() {
    if (!hasNextByte()) throw new InputMismatchException("Next token is not a byte");

    return Byte.parseByte(next());
  }

  public boolean hasNextDouble() {
    return hasNext(floatPattern);
  }

  public double nextDouble() {
    if (!hasNextDouble()) throw new InputMismatchException("Next token is not a double");

    return Double.parseDouble(next());
  }

  public boolean hasNextFloat() {
    return hasNext(floatPattern);
  }

  public float nextFloat() {
    if (!hasNextFloat()) throw new InputMismatchException("Next token is not a float");

    return Float.parseFloat(next());
  }

  public boolean hasNextInt() {
    return hasNext(integerPattern);
  }

  public int nextInt() {
    if (!hasNextInt()) throw new InputMismatchException("Next token is not a int");

    return Integer.parseInt(next());
  }

  public boolean hasNextLine() {
    if (closed && currentPosition == bufferFilledLength) return false;

    if (nextDelimiterStart == -1 || nextDelimiterWithPattern != endLinePattern) {
      searchNextTo(endLinePattern, true);
      nextDelimiterWithPattern = endLinePattern;
    }

    return currentPosition != bufferFilledLength;
  }

  public String nextLine() {
    if (!hasNextLine()) throw new InputMismatchException("No new line");

    String result = new String(buf, currentPosition, nextDelimiterStart - currentPosition);
    currentPosition = nextDelimiterEnd;
    nextDelimiterStart = nextDelimiterEnd = -1;
    return result;
  }

  public boolean hasNextLong() {
    return hasNext(integerPattern);
  }

  public long nextLong() {
    if (!hasNextLong()) throw new InputMismatchException("Next token is not a long");

    return Long.parseLong(next());
  }

  public boolean hasNextShort() {
    return hasNext(integerPattern);
  }

  public short nextShort() {
    if (!hasNextShort()) throw new InputMismatchException("Next token is not a short");

    return Short.parseShort(next());
  }

  public Scanner reset() {
    return useDelimiter(whiteSpacePattern);
  }

  public Scanner skip(String pattern) {
    return skip(Pattern.compile(pattern));
  }

  public Scanner skip(Pattern pattern) {
    if (closed && currentPosition == bufferFilledLength)
      throw new NoSuchElementException("No more token");

    searchNextTo(pattern, true);
    if (nextDelimiterStart != currentPosition) {
      throw new NoSuchElementException("The specified pattern was not found");
    }

    currentPosition = nextDelimiterEnd;
    nextDelimiterStart = nextDelimiterEnd = -1;
    return this;
  }

  public String findInLine(String pattern) {
    return findInLine(Pattern.compile(pattern));
  }

  public String findInLine(Pattern pattern) {
    if (!hasNextLine()) {
      return null;
    }

    matcher =
        pattern.matcher(new String(buf, currentPosition, nextDelimiterStart - currentPosition));
    if (matcher.find()) {
      nextLine();
      return matcher.group();
    } else {
      nextDelimiterStart = nextDelimiterEnd = -1;
      return null;
    }
  }

  public MatchResult match() {
    if (matcher == null) throw new IllegalStateException("No match result is available");

    return matcher;
  }
}
