package java.nio.file;

import static jsweet.util.Lang.array;
import static jsweet.util.Lang.string;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class Path implements Comparable<Path>, Iterable<Path> {
  // move to filesystem
  static final String PATH_SEPARATOR = System.getProperty("file.separator");

  private String fullPath;

  Path(String fullPath) {
    this.fullPath = fullPath;
  }

  @Override
  public int compareTo(Path path) {
    return toString().compareTo(path.toString());
  }

  public boolean isAbsolute() {
    return Objects.equals(PATH_SEPARATOR, "/")
        ? fullPath.length() > 0 && Objects.equals(fullPath.substring(0, 1), PATH_SEPARATOR)
        : fullPath.length() >= 3 && Objects.equals(fullPath.substring(1, 3), ":\\");
  }

  public Path getFileName() {
    return new Path(
        fullPath.substring(fullPath.lastIndexOf(PATH_SEPARATOR) + PATH_SEPARATOR.length()));
  }

  public Path getParent() {
    return new Path(fullPath.substring(0, fullPath.lastIndexOf(PATH_SEPARATOR)));
  }

  public Path resolve(Path other) {
    if (other.isAbsolute()) return other;
    return new Path(fullPath + "/" + other.fullPath);
  }

  public Path resolve(String other) {
    return resolve(Paths.get(other));
  }

  public Path toAbsolutePath() {
    return Paths.get(System.getProperty("user.dir")).resolve(this);
  }

  @Override
  public Iterator<Path> iterator() {
    return Arrays.asList(
            array(
                string(fullPath)
                    .split(PATH_SEPARATOR)
                    .map(
                        (str, i, arr) ->
                            isAbsolute() && i == 0
                                ? new Path(string(str) + PATH_SEPARATOR)
                                : new Path(string(str)))))
        .iterator();
  }

  @Override
  public String toString() {
    return fullPath;
  }
}
