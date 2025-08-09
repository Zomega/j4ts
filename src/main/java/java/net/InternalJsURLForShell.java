package java.net;

import def.js.Error;
import def.js.Object;

public class InternalJsURLForShell {
  public final String href;
  public final String protocol;
  public final String username;
  public final String password;
  public final String hostname;
  public final Integer port;
  public final String pathname;
  public final String search;
  public final String hash;

  public InternalJsURLForShell(String data) {
    this.href = data;

    int protocolEnd = data.indexOf("://");
    if (protocolEnd == -1) throw new Error("Wrong URL, no protocol");

    this.protocol = data.substring(0, protocolEnd);

    int userPassEnd = data.indexOf("@", protocolEnd + 3);
    int userEnd = userPassEnd == -1 ? -1 : data.indexOf(":", protocolEnd + 3);
    if (userEnd > userPassEnd) userEnd = -1;

    this.username =
        userPassEnd == -1
            ? null
            : userEnd == -1
                ? data.substring(protocolEnd + 3, userPassEnd)
                : data.substring(protocolEnd + 3, userEnd);
    this.password =
        userPassEnd == -1 || userEnd == -1 ? null : data.substring(userPassEnd + 1, userEnd);

    int hostStart = userPassEnd == -1 ? protocolEnd + 3 : userPassEnd + 1;

    int hostPortEnd = data.indexOf("/", hostStart);
    int portStart = data.indexOf(":", hostStart);

    this.hostname =
        portStart == -1 || portStart > hostPortEnd
            ? data.substring(hostStart, hostPortEnd)
            : data.substring(hostStart, portStart);

    this.port =
        portStart != -1 && portStart < hostPortEnd
            ? Integer.parseInt(data.substring(portStart + 1, hostPortEnd))
            : null;

    int searchStart = data.indexOf("?", hostPortEnd);
    int hashStart = data.indexOf("#", hostPortEnd);

    this.search =
        searchStart != -1
            ? hashStart == -1
                ? data.substring(searchStart + 1)
                : data.substring(searchStart + 1, hashStart)
            : null;
    this.hash = hashStart != -1 ? data.substring(hashStart + 1) : null;

    this.pathname =
        searchStart == -1 && hashStart == -1
            ? data.substring(hostPortEnd + 1)
            : data.substring(hostPortEnd + 1, searchStart == -1 ? hashStart : searchStart);
  }

  public InternalJsURLForShell(String data, Object url) {
    this(url.$get("href") + data);
  }
}
