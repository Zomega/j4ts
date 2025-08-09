package javaemul.internal;

import static jsweet.util.Lang.$insert;

public class ObjectHelper {

  // TODO: JSweet is missing the for .. in loop construct to remove the native
  // implementation here...

  public static Object clone(Object obj) {

    $insert("var copy");

    // Handle the 3 simple types, and null or undefined
    $insert("if (null == obj || \"object\" != typeof obj) return obj");

    // Handle Date
    $insert(
        "if (obj instanceof Date) { copy = new Date(); copy.setTime(obj.getTime()); return copy;"
            + " }");

    // Handle Array
    $insert(
        "if (obj instanceof Array) { copy = []; for (var i = 0, len = obj.length; i < len; i++) {"
            + " copy[i] = ObjectHelper.clone(obj[i]); } return copy; }");

    // Handle Object
    $insert(
        "if (obj instanceof Object) { copy = {}; for (var attr in obj) { if"
            + " (obj.hasOwnProperty(attr)) copy[attr] = ObjectHelper.clone(obj[attr]); } return"
            + " copy; }");

    throw new Error("Unable to copy obj! Its type isn't supported.");
  }
}
