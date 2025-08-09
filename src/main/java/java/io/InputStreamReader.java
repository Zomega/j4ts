package java.io;

import static jsweet.util.Lang.string;

import java.nio.charset.Charset;

/** JSweet implementation. */
public class InputStreamReader extends Reader {

  InputStream in;

  public InputStreamReader(InputStream in) {
    super(in);
    this.in = in;
  }

  public InputStreamReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
    super(in);
    this.in = in;
  }

  public InputStreamReader(InputStream in, Charset cs) {
    super(in);
    this.in = in;
    if (cs == null) throw new NullPointerException("charset");
  }

  public int read(char cbuf[], int offset, int length) throws IOException {
    byte[] buf = new byte[length - offset];
    int success = in.read(buf, 0, length);

    if (success > 0) {
      for (int i = 0; i < success; ++i) {
        cbuf[i + offset] =
            string(def.js.String.fromCharCode(buf[i]))
                .charAt(0); // TODO switch to charset converter
      }
    }

    return success;
  }

  public boolean ready() throws IOException {
    return in.available() > 0;
  }

  public void close() throws IOException {
    in.close();
  }
}
