package java.nio.file;


import static jsweet.util.Lang.array;
import static jsweet.util.Lang.string;

public class Paths {
  private Paths() {}

  public static Path get(String... paths) {
    return new Path(string(array(paths).join(Path.PATH_SEPARATOR)));
  }
}
