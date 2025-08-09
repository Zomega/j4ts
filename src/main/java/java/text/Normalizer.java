package java.text;

import jsweet.util.Lang;

public class Normalizer {

  public static enum Form {

    /** Canonical decomposition. */
    NFD,

    /** Canonical decomposition, followed by canonical composition. */
    NFC,

    /** Compatibility decomposition. */
    NFKD,

    /** Compatibility decomposition, followed by canonical composition. */
    NFKC
  }

  public static String normalize(CharSequence src, Form form) {
    return Lang.string(src.toString()).normalize(form.name()).toString();
  }
}
