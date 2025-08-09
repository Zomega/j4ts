/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package java.lang;

import static def.dom.Globals.*;
import static def.js.Globals.eval;
import static javaemul.internal.InternalPreconditions.checkNotNull;
import static jsweet.util.Lang.*;

import def.js.Function;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javaemul.internal.ArrayHelper;
import javaemul.internal.DateUtil;
import javaemul.internal.HashCodes;

public final class System {
  public static final boolean ENVIRONMENT_IS_WEB = Objects.equals(typeof(window), "object");
  public static final boolean ENVIRONMENT_IS_WORKER =
      Objects.equals(typeof($insert("importScripts")), "function");
  public static final boolean ENVIRONMENT_IS_NODE =
      !ENVIRONMENT_IS_WEB
          && !ENVIRONMENT_IS_WORKER
          && Objects.equals(eval("typeof process"), "object")
          && Objects.equals(eval("typeof require"), "function");
  public static final boolean ENVIRONMENT_IS_SHELL =
      !ENVIRONMENT_IS_WEB && !ENVIRONMENT_IS_WORKER && !ENVIRONMENT_IS_NODE;

  public static Map<String, String> propertyMap;
  public static PrintStream err;
  public static PrintStream out;
  public static InputStream in;

  static {
    propertyMap = new HashMap<>();
    propertyMap.put("java.vendor", "JSweet");
    propertyMap.put("java.vendor.url", "http://www.jsweet.org");
    propertyMap.put("java.version", "jsweet");

    String tmpDir = "";
    String lineSeparator = "\n";
    String fileSeparator = "/";
    String userHome = "";
    String userName = "";
    String osArch = "";

    if (ENVIRONMENT_IS_WEB) {
      propertyMap.put("os.name", "WEB");
      propertyMap.put("os.version", navigator.userAgent);

      String pathname = document.location.pathname;
      propertyMap.put("user.dir", pathname.substring(0, pathname.lastIndexOf("/")));

    } else if (ENVIRONMENT_IS_WORKER) {
      propertyMap.put("os.name", "WEB-WORKER");
      propertyMap.put("os.version", navigator.userAgent);

      String pathname = "/"; // NO document IN WEB WORKER!
      propertyMap.put("user.dir", pathname.substring(0, pathname.lastIndexOf("/")));

    } else if (ENVIRONMENT_IS_NODE) {
      def.js.Object os = eval("global.os || (global.os = require(\"os\"))");
      def.js.Object path = eval("global.path || (global.path = require(\"path\"))");

      propertyMap.put("os.name", "NODE");
      propertyMap.put("os.version", eval("process.version"));

      propertyMap.put("user.dir", eval("process.cwd()"));

      tmpDir = ((Function) os.$get("tmpdir")).$apply();
      lineSeparator = os.$get("EOL");
      fileSeparator = path.$get("sep");
      userHome = ((Function) os.$get("homedir")).$apply();
      userName = object(((Function) os.$get("userInfo")).$apply()).$get("username");
      osArch = ((Function) os.$get("arch")).$apply();

    } else { // SHELL
      console = any(new def.js.Object());
      console.$set("info", $insert("print"));
      console.$set(
          "error",
          Objects.equals(eval("typeof printErr"), "undefined")
              ? $insert("print")
              : eval("printErr"));

      propertyMap.put("os.name", "SHELL");

      String runnerName = "UNKNOWN";
      String userDir = ".";

      if (Objects.equals(eval("typeof environment"), "object")) {
        runnerName = "RHINO";

        userDir = eval("environment[\"user.dir\"]");

        lineSeparator = eval("environment[\"line.separator\"]");
        fileSeparator = eval("environment[\"file.separator\"]");
        userHome = eval("environment[\"user.name\"]");
        userName = eval("environment[\"user.home\"]");
        osArch = eval("environment[\"os.arch\"]");

      } else if (Objects.equals(eval("typeof jscOptions"), "function")) {
        runnerName = "JSC";

        // userDir = eval("callerSourceOrigin()");
        // userDir = userDir.substring(0, userDir.lastIndexOf("/"));

      }

      propertyMap.put("os.version", runnerName);
      propertyMap.put("user.dir", userDir);
    }

    propertyMap.put("java.io.tmpdir", tmpDir);

    propertyMap.put("line.separator", lineSeparator);
    propertyMap.put("file.separator", fileSeparator);

    propertyMap.put("user.home", userHome);
    propertyMap.put("user.name", userName);

    propertyMap.put("os.arch", osArch);

    out =
        new PrintStream(
            new OutputStream() {
              private final String sep = propertyMap.get("line.separator");

              String toOut = "";

              @Override
              public void write(int i) throws IOException {
                toOut +=
                    string(
                        def.js.String.fromCharCode(
                            i)); // TODO it handles as a character, and not as a byte

                if (toOut.endsWith(sep)) {
                  flush();
                }
              }

              @Override
              public void flush() throws IOException {
                super.flush();

                if (!toOut.isEmpty()) {
                  if (toOut.endsWith(sep)) {
                    toOut = toOut.substring(0, toOut.length() - sep.length());
                  }
                  console.info(toOut);
                  toOut = "";
                }
              }
            });

    err =
        new PrintStream(
            new OutputStream() {
              private final String sep = propertyMap.get("line.separator");

              String toOut = "";

              @Override
              public void write(int i) throws IOException {
                toOut +=
                    string(
                        def.js.String.fromCharCode(
                            i)); // TODO it handles as a character, and not as a byte

                if (toOut.endsWith(sep)) {
                  flush();
                }
              }

              @Override
              public void flush() throws IOException {
                super.flush();

                if (!toOut.isEmpty()) {
                  if (toOut.endsWith(sep)) {
                    toOut = toOut.substring(0, toOut.length() - sep.length());
                  }
                  console.error(toOut);
                  toOut = "";
                }
              }
            });

    in =
        new InputStream() {
          private char[] readData;
          private int where = 0;
          private final String sep = propertyMap.get("line.separator");
          private final Supplier<String> readerFunction =
              () -> {
                String result = null;
                if (System.ENVIRONMENT_IS_NODE) {
                  def.js.Object fs = eval("global.fs || (global.fs = require(\"fs\"))");

                  int BUFSIZE = 256;
                  def.js.Object buf = $new(eval("Buffer"), BUFSIZE);

                  int fd = eval("process.stdin.fd");
                  boolean usingDevice = false;
                  try {
                    fd = ((Function) fs.$get("openSync")).$apply(object("/dev/stdin"), object("r"));
                    usingDevice = true;
                  } catch (Exception ignored) {
                  }

                  int bytesRead = 0;
                  try {
                    bytesRead =
                        ((Function) fs.$get("readSync"))
                            .$apply(any(fd), buf, any(0), any(BUFSIZE), null);
                  } catch (Exception e) {
                    if (e.toString().indexOf("EOF") == -1) throw e;
                  }
                  if (usingDevice) ((Function) fs.$get("closeSync")).$apply(any(fd));
                  if (bytesRead > 0)
                    result = array((byte[]) any(buf)).slice(0, bytesRead).toString();

                } else if (System.ENVIRONMENT_IS_WEB || System.ENVIRONMENT_IS_WORKER) {
                  if (Objects.equals(typeof(window.$get("prompt")), "function")) {
                    result = window.prompt("Input: ");
                    if (result != null) {
                      result += sep;
                    }
                  }
                } else if (Objects.equals(typeof(eval("readline")), "function")) {
                  result = eval("readline()");
                  if (result != null) {
                    result += sep;
                  }
                }
                return result;
              };

          @Override
          public int read() {
            if (readData == null) {
              readData = readerFunction.get().toCharArray();
              where = 0;
            } else {
              ++where;
            }
            if (where == readData.length) {
              readData = null;
              where = 0;
              return -1;
            }
            return readData[where]; // TODO it returns a character, and not a byte!!
          }
        };
  }

  public static void arraycopy(Object src, int srcOfs, Object dest, int destOfs, int len) {
    checkNotNull(src, "src");
    checkNotNull(dest, "dest");

    // TODO: right now, all checks are disabled but could be reactivated
    // when appropriate support is available

    // Class<?> srcType = src.getClass();
    // Class<?> destType = dest.getClass();

    // TODO: support isArray() as a macro
    // checkArrayType(srcType.isArray(), "srcType is not an array");
    // checkArrayType(destType.isArray(), "destType is not an array");

    // TODO: support getComponentType() as a macro
    // Class<?> srcComp = srcType.getComponentType();
    // Class<?> destComp = destType.getComponentType();
    // checkArrayType(arrayTypeMatch(srcComp, destComp), "Array types don't
    // match");

    int srclen = ArrayHelper.getLength(src);
    int destlen = ArrayHelper.getLength(dest);
    if (srcOfs < 0 || destOfs < 0 || len < 0 || srcOfs + len > srclen || destOfs + len > destlen) {
      throw new IndexOutOfBoundsException();
    }
    /*
     * If the arrays are not references or if they are exactly the same
     * type, we can copy them in native code for speed. Otherwise, we have
     * to copy them in Java so we get appropriate errors.
     */
    // if ((!srcComp.isPrimitive() || srcComp.isArray()) &&
    // !srcType.equals(destType)) {
    // // copy in Java to make sure we get ArrayStoreExceptions if the
    // // values
    // // aren't compatible
    // Object[] srcArray = (Object[]) src;
    // Object[] destArray = (Object[]) dest;
    // if (src == dest && srcOfs < destOfs) {
    // // TODO(jat): how does backward copies handle failures in the
    // // middle?
    // // copy backwards to avoid destructive copies
    // srcOfs += len;
    // for (int destEnd = destOfs + len; destEnd-- > destOfs;) {
    // destArray[destEnd] = srcArray[--srcOfs];
    // }
    // } else {
    // for (int destEnd = destOfs + len; destOfs < destEnd;) {
    // destArray[destOfs++] = srcArray[srcOfs++];
    // }
    // }
    // } else
    if (len > 0) {
      ArrayHelper.copy(src, srcOfs, dest, destOfs, len);
    }
  }

  public static long currentTimeMillis() {
    return (long) DateUtil.now();
  }

  public static void gc() {
    function(
            () -> {
              Function gcFun = eval("this.gc");
              if (Objects.equals(typeof(gcFun), "function")) {
                gcFun.$apply();
              }
            })
        .apply(null); // this forces to use "global" this context
  }

  public static String getProperty(String key) {
    return propertyMap == null ? null : propertyMap.get(key);
  }

  public static String getProperty(String key, String def) {
    String prop = getProperty(key);
    return prop == null ? def : prop;
  }

  public static int identityHashCode(Object o) {
    return HashCodes.getIdentityHashCode(o);
  }

  public static void setErr(PrintStream err) {
    System.err = err;
  }

  public static void setOut(PrintStream out) {
    System.out = out;
  }

  public static String lineSeparator() {
    return getProperty("line.separator");
  }

  public static void exit(int status) {
    if (ENVIRONMENT_IS_NODE) {
      eval("process.exit(" + status + ")");
    } else if (ENVIRONMENT_IS_WEB) {
      window.close();
    } else if (ENVIRONMENT_IS_WORKER) {
      self.close(); // deprecated
      // nicely worker need to ask the parent to terminate it
    } else {
      if (Objects.equals(typeof(eval("quit")), "function")) {
        eval("quit(" + status + ")");
      }
      if (Objects.equals(typeof(eval("exit")), "function")) {
        eval("exit(" + status + ")");
      }
    }
  }

  //	private static boolean arrayTypeMatch(Class<?> srcComp, Class<?> destComp) {
  //		if (srcComp.isPrimitive()) {
  //			return srcComp.equals(destComp);
  //		} else {
  //			return !destComp.isPrimitive();
  //		}
  //	}
}
