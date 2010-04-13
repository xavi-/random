/*
  +---------------------------------------------------------------------------+
  | Facebook Development Platform Java Client                                 |
  +---------------------------------------------------------------------------+
  | Copyright (c) 2007 Facebook, Inc.                                         |
  | All rights reserved.                                                      |
  |                                                                           |
  | Redistribution and use in source and binary forms, with or without        |
  | modification, are permitted provided that the following conditions        |
  | are met:                                                                  |
  |                                                                           |
  | 1. Redistributions of source code must retain the above copyright         |
  |    notice, this list of conditions and the following disclaimer.          |
  | 2. Redistributions in binary form must reproduce the above copyright      |
  |    notice, this list of conditions and the following disclaimer in the    |
  |    documentation and/or other materials provided with the distribution.   |
  |                                                                           |
  | THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR      |
  | IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES |
  | OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.   |
  | IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,          |
  | INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT  |
  | NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, |
  | DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY     |
  | THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT       |
  | (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF  |
  | THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.         |
  +---------------------------------------------------------------------------+
  | For help with this library, contact developers-help@facebook.com          |
  +---------------------------------------------------------------------------+
 */

package com.facebook.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FacebookRestClient {
  public static final String TARGET_API_VERSION = "1.0";
  public static final String ERROR_TAG = "error_response";
  public static final String FB_SERVER = "api.facebook.com/restserver.php";
  public static final String SERVER_ADDR = "http://" + FB_SERVER;
  public static final String HTTPS_SERVER_ADDR = "https://" + FB_SERVER;
  public static URL SERVER_URL = null;
  public static URL HTTPS_SERVER_URL = null;
  static {
    try {
      SERVER_URL = new URL(SERVER_ADDR);
      HTTPS_SERVER_URL = new URL(HTTPS_SERVER_ADDR);
    }
    catch (MalformedURLException e) {
      System.err.println("MalformedURLException: " + e.getMessage());
      System.exit(1);
    }
  }

  private final String _secret;
  private final String _apiKey;
  private final URL _serverUrl;
  
  private String _sessionKey; // filled in when session is established
  private boolean _isDesktop = false;
  private String _sessionSecret; // only used for desktop apps 
  private int _userId;

  public static int NUM_AUTOAPPENDED_PARAMS = 5;
  private static boolean DEBUG = false;
  private Boolean _debug = null;

  public static void setDebugAll(boolean isDebug) {
    FacebookRestClient.DEBUG = isDebug;
  }

  public void setDebug(boolean isDebug) {
    _debug = isDebug;
  }

  public boolean isDebug() {
    return (null == _debug) ? FacebookRestClient.DEBUG : _debug.booleanValue();
  }

  public boolean isDesktop() {
    return this._isDesktop;
  }

  public void setIsDesktop(boolean isDesktop) {
    this._isDesktop = isDesktop;
  }

  public FacebookRestClient(String apiKey, String secret) {
    this(SERVER_URL, apiKey, secret, null);
  }

  public FacebookRestClient(String apiKey, String secret, String sessionKey) {
    this(SERVER_URL, apiKey, secret, sessionKey);
  }

  public FacebookRestClient(String serverAddr, String apiKey, String secret,
                            String sessionKey) throws MalformedURLException {
    this(new URL(serverAddr), apiKey, secret, sessionKey);
  }

  public FacebookRestClient(URL serverUrl, String apiKey, String secret, String sessionKey) {
    _sessionKey = sessionKey;
    _apiKey = apiKey;
    _secret = secret;
    _serverUrl = (null != serverUrl) ? serverUrl : SERVER_URL;
  }

  /**
   * Prints out the DOM tree.
   */
  public static void printDom(Node n, String prefix) {
    String outString = prefix;
    if (n.getNodeType() == Node.TEXT_NODE) {
      outString += "'" + n.getTextContent().trim() + "'";
    }
    else {
      outString += n.getNodeName();
    }
    System.out.println(outString);
    NodeList children = n.getChildNodes();
    int length = children.getLength();
    for (int i = 0; i < length; i++) {
      FacebookRestClient.printDom(children.item(i), prefix + "  ");
    }
  }

  protected class Pair<N, V> {
    public N first;
    public V second;

    public Pair(N name, V value) {
      this.first = name;
      this.second = value;
    }
  }

  // could add a thread-safe version that uses StringBuffer as well

  private static CharSequence delimit(Collection iterable) {
    if (iterable == null || iterable.isEmpty())
      return null;

    StringBuilder buffer = new StringBuilder();
    boolean notFirst = false;
    for (Object item: iterable) {
      if (notFirst)
        buffer.append(",");
      else
        notFirst = true;
      buffer.append(item.toString());
    }
    return buffer;
  }

  private static List<String> convert(Collection<Map.Entry<String, CharSequence>> entries) {
    List<String> result = new ArrayList<String>(entries.size());
    for (Map.Entry<String, CharSequence> entry: entries)
      result.add(entry.getKey() + "=" + entry.getValue());
    return result;
  }

  private static CharSequence delimit(Collection<Map.Entry<String, CharSequence>> entries,
                                      CharSequence delimiter, CharSequence equals) {
    if (entries == null || entries.isEmpty())
      return null;

    StringBuilder buffer = new StringBuilder();
    boolean notFirst = false;
    for (Map.Entry entry: entries) {
      if (notFirst)
        buffer.append(delimiter);
      else
        notFirst = true;
      buffer.append(entry.getKey()).append(equals).append(entry.getValue());
    }
    return buffer;
  }

  /**
   * Call the specified method, with the given parameters, and return a DOM tree with the results.
   *
   * @param method the fieldName of the method
   * @param paramPairs a list of arguments to the method
   * @throws Exception with a description of any errors given to us by the server.
   */
  protected Document callMethod(FacebookMethod method,
                                Pair<String, CharSequence>... paramPairs) throws FacebookException,
                                                                                 IOException {
    return callMethod(method, Arrays.asList(paramPairs));
  }

  /**
   * Call the specified method, with the given parameters, and return a DOM tree with the results.
   *
   * @param method the fieldName of the method
   * @param paramPairs a list of arguments to the method
   * @throws Exception with a description of any errors given to us by the server.
   */
  protected Document callMethod(FacebookMethod method,
                                Collection<Pair<String, CharSequence>> paramPairs) throws FacebookException,
                                                                                          IOException {
    HashMap<String, CharSequence> params =
      new HashMap<String, CharSequence>(2 * method.numTotalParams());

    params.put("method", method.methodName());
    params.put("api_key", _apiKey);
    params.put("v", TARGET_API_VERSION);
    if (method.requiresSession()) {
      params.put("call_id", Long.toString(System.currentTimeMillis()));
      params.put("session_key", _sessionKey);
    }
    CharSequence oldVal;
    for (Pair<String, CharSequence> p: paramPairs) {
      oldVal = params.put(p.first, p.second);
      if (oldVal != null)
        System.err.printf("For parameter %s, overwrote old value %s with new value %s.", p.first,
                          oldVal, p.second);
    }

    assert (!params.containsKey("sig"));
    params.put("sig", generateSignature(convert(params.entrySet()), method.requiresSession()));

    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      boolean doHttps = this.isDesktop() && FacebookMethod.AUTH_GET_SESSION.equals(method);
      InputStream data = postRequest(method.methodName(), params, doHttps);
      //      if (isDebug())
      //        printStream(data);
      Document doc = builder.parse(data);
      doc.normalizeDocument();
      stripEmptyTextNodes(doc);

      if (isDebug())
        FacebookRestClient.printDom(doc, method.methodName() + "| "); // TEST
      NodeList errors = doc.getElementsByTagName(ERROR_TAG);
      if (errors.getLength() > 0) {
        int errorCode =
          Integer.parseInt(errors.item(0).getFirstChild().getFirstChild().getTextContent());
        String message = errors.item(0).getFirstChild().getNextSibling().getTextContent();
        throw new FacebookException(errorCode, message);
      }
      return doc;
    }
    catch (javax.xml.parsers.ParserConfigurationException ex) {
      System.err.println("huh?" + ex);
    }
    catch (org.xml.sax.SAXException ex) {
      throw new IOException("error parsing xml");
    }
    return null;
  }

  /**
   * Hack...since DOM reads newlines as textnodes we want to strip out those
   * nodes to make it easier to use the tree.
   */
  private static void stripEmptyTextNodes(Node n) {
    NodeList children = n.getChildNodes();
    int length = children.getLength();
    for (int i = 0; i < length; i++) {
      Node c = children.item(i);
      if (!c.hasChildNodes() && c.getNodeType() == Node.TEXT_NODE &&
          c.getTextContent().trim().length() == 0) {
        n.removeChild(c);
        i--;
        length--;
        children = n.getChildNodes();
      }
      else {
        stripEmptyTextNodes(c);
      }
    }
  }

  private String generateSignature(List<String> params, boolean requiresSession) {
    StringBuffer buffer = new StringBuffer();
    Collections.sort(params);
    for (String param: params) {
      buffer.append(param);
    }

    // for desktop apps, use the session secret obtained after authentication
    if (isDesktop() && requiresSession)
      buffer.append(this._sessionSecret);
    else
      buffer.append(this._secret);
    try {
      java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
      StringBuffer result = new StringBuffer();
      for (byte b: md.digest(buffer.toString().getBytes())) {
        result.append(Integer.toHexString((b & 0xf0) >>> 4));
        result.append(Integer.toHexString(b & 0x0f));
      }
      return result.toString();
    }
    catch (java.security.NoSuchAlgorithmException ex) {
      System.err.println("huh?" + ex);
      return "";
    }
  }

  private InputStream postRequest(CharSequence method, Map<String, CharSequence> params,
                                  boolean doHttps) throws IOException {
    CharSequence buffer = (null == params) ? "" : delimit(params.entrySet(), "&", "=");
    URL serverUrl = (doHttps) ? HTTPS_SERVER_URL : _serverUrl;
    if (isDebug()) {
      System.out.print(method);
      System.out.print(" POST: ");
      System.out.print(serverUrl.toString());
      System.out.print("/");
      System.out.println(buffer);
      System.out.flush();
    }

    HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
    try {
      conn.setRequestMethod("POST");
    }
    catch (ProtocolException ex) {
      System.err.println("huh?" + ex);
    }
    conn.setDoOutput(true);
    conn.connect();
    conn.getOutputStream().write(buffer.toString().getBytes());

    return conn.getInputStream();
  }

  /**
   * Returns all visible events according to the filters specified. This may be used to find all events of a user, or to query specific eids.
   * @param eventIds filter by these event ID's (optional)
   * @param userId filter by this user only (optional)
   * @param startTime UTC lower bound (optional)
   * @param endTime UTC upper bound (optional)
   * @return Document of events
   */
  public Document events_get(Integer userId, Collection<Long> eventIds, Long startTime,
                             Long endTime) throws FacebookException, IOException {
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(FacebookMethod.EVENTS_GET.numParams());

    boolean hasUserId = null != userId && 0 != userId;
    boolean hasEventIds = null != eventIds && !eventIds.isEmpty();
    boolean hasStart = null != startTime && 0 != startTime;
    boolean hasEnd = null != endTime && 0 != endTime;

    if (hasUserId)
      params.add(new Pair<String, CharSequence>("uid", Integer.toString(userId)));
    if (hasEventIds)
      params.add(new Pair<String, CharSequence>("eids", delimit(eventIds)));
    if (hasStart)
      params.add(new Pair<String, CharSequence>("start_time", startTime.toString()));
    if (hasEnd)
      params.add(new Pair<String, CharSequence>("end_time", endTime.toString()));
    return this.callMethod(FacebookMethod.EVENTS_GET, params);
  }

  /**
   * Retrieves the membership list of an event
   * @param eventId event id
   * @return Document consisting of four membership lists corresponding to RSVP status, with keys
   *  'attending', 'unsure', 'declined', and 'not_replied'
   */
  public Document events_getMembers(Number eventId) throws FacebookException, IOException {
    assert (null != eventId);
    return this.callMethod(FacebookMethod.EVENTS_GET_MEMBERS,
                           new Pair<String, CharSequence>("eid", eventId.toString()));
  }


  /**
   * Retrieves the friends of the currently logged in user.
   * @return array of friends
   */
  public Document friends_areFriends(int userId1, int userId2) throws FacebookException,
                                                                      IOException {
    return this.callMethod(FacebookMethod.FRIENDS_ARE_FRIENDS,
                           new Pair<String, CharSequence>("uids1", Integer.toString(userId1)),
                           new Pair<String, CharSequence>("uids2", Integer.toString(userId2)));
  }

  public Document friends_areFriends(Collection<Integer> userIds1,
                                     Collection<Integer> userIds2) throws FacebookException,
                                                                          IOException {
    assert (userIds1 != null && userIds2 != null);
    assert (!userIds1.isEmpty() && !userIds2.isEmpty());
    assert (userIds1.size() == userIds2.size());

    return this.callMethod(FacebookMethod.FRIENDS_ARE_FRIENDS,
                           new Pair<String, CharSequence>("uids1", delimit(userIds1)),
                           new Pair<String, CharSequence>("uids2", delimit(userIds2)));
  }

  /**
   * Retrieves the friends of the currently logged in user.
   * @return array of friends
   */
  public Document friends_get() throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.FRIENDS_GET);
  }

  /**
   * Retrieves the friends of the currently logged in user, who are also users
   * of the calling application.
   * @return array of friends
   */
  public Document friends_getAppUsers() throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.FRIENDS_GET_APP_USERS);
  }

  /**
   * Retrieves the requested info fields for the requested set of users.
   * @param userIds a collection of user ids to get info for
   * @param fields a set of strings describing the info fields desired, such as "last_name", "sex"
   * @return an array of arrays of info fields, which may themselves be arrays :)
   */
  public Document users_getInfo(Collection<Integer> userIds,
                                EnumSet<ProfileField> fields) throws FacebookException,
                                                                     IOException {
    // assertions test for invalid params
    assert (userIds != null);
    assert (fields != null);
    assert (!fields.isEmpty());

    return this.callMethod(FacebookMethod.USERS_GET_INFO,
                           new Pair<String, CharSequence>("uids", delimit(userIds)),
                           new Pair<String, CharSequence>("fields", delimit(fields)));
  }

  public int users_getLoggedInUser() throws FacebookException, IOException {
    Document d = this.callMethod(FacebookMethod.USERS_GET_LOGGED_IN_USER);
    return Integer.parseInt(d.getFirstChild().getTextContent());
  }

  public Document users_getInfo(Collection<Integer> userIds,
                                Set<CharSequence> fields) throws FacebookException, IOException {
    // assertions test for invalid params
    assert (userIds != null);
    assert (fields != null);
    assert (!fields.isEmpty());

    return this.callMethod(FacebookMethod.USERS_GET_INFO,
                           new Pair<String, CharSequence>("uids", delimit(userIds)),
                           new Pair<String, CharSequence>("fields", delimit(fields)));
  }

  /**
   * Used to retrieve photo objects using the search parameters (one or more of the
   * parameters must be provided).
   *
   * @param subjId retrieve from photos associated with this user (optional).
   * @param albumId retrieve from photos from this album (optional)
   * @param photoIds retrieve from this list of photos (optional)
   *
   * @return an Document of photo objects.
   */
  public Document photos_get(Integer subjId, Long albumId,
                             Collection<Long> photoIds) throws FacebookException, IOException {
    ArrayList<Pair<String, CharSequence>> params =
      new ArrayList<Pair<String, CharSequence>>(FacebookMethod.PHOTOS_GET.numParams());

    boolean hasUserId = null != subjId && 0 != subjId;
    boolean hasAlbumId = null != albumId && 0 != albumId;
    boolean hasPhotoIds = null != photoIds && !photoIds.isEmpty();
    assert (hasUserId || hasAlbumId || hasPhotoIds);

    if (hasUserId)
      params.add(new Pair<String, CharSequence>("subj_id", Integer.toString(subjId)));
    if (hasAlbumId)
      params.add(new Pair<String, CharSequence>("aid", Long.toString(albumId)));
    if (hasPhotoIds)
      params.add(new Pair<String, CharSequence>("pids", delimit(photoIds)));

    return this.callMethod(FacebookMethod.PHOTOS_GET, params);
  }

  public Document photos_get(Long albumId, Collection<Long> photoIds) throws FacebookException,
                                                                             IOException {
    return photos_get(null, albumId, photoIds);
  }

  public Document photos_get(Integer subjId, Collection<Long> photoIds) throws FacebookException,
                                                                               IOException {
    return photos_get(subjId, null, photoIds);
  }

  public Document photos_get(Integer subjId, Long albumId) throws FacebookException, IOException {
    return photos_get(subjId, albumId, null);
  }

  public Document photos_get(Collection<Long> photoIds) throws FacebookException, IOException {
    return photos_get(null, null, photoIds);
  }

  public Document photos_get(Long albumId) throws FacebookException, IOException {
    return photos_get(null, albumId, null);
  }

  public Document photos_get(Integer subjId) throws FacebookException, IOException {
    return photos_get(subjId, null, null);
  }

  /**
   * Retrieves album metadata. Pass a user id and/or a list of album ids to specify the albums
   * to be retrieved (at least one must be provided)
   *
   * @param userId retrieve metadata for albums created the id of the user whose album you wish  (optional).
   * @param albumIds the ids of albums whose metadata is to be retrieved
   * @return album objects.
   */
  public Document photos_getAlbums(Integer userId,
                                   Collection<Long> albumIds) throws FacebookException,
                                                                     IOException {
    boolean hasUserId = null != userId && userId != 0;
    boolean hasAlbumIds = null != albumIds && !albumIds.isEmpty();
    assert (hasUserId || hasAlbumIds); // one of the two must be provided

    if (hasUserId)
      return (hasAlbumIds) ?
             this.callMethod(FacebookMethod.PHOTOS_GET_ALBUMS, new Pair<String, CharSequence>("uid",
                                                                                              Integer.toString(userId)),
                             new Pair<String, CharSequence>("aids", delimit(albumIds))) :
             this.callMethod(FacebookMethod.PHOTOS_GET_ALBUMS,
                             new Pair<String, CharSequence>("uid", Integer.toString(userId)));
    else
      return this.callMethod(FacebookMethod.PHOTOS_GET_ALBUMS,
                             new Pair<String, CharSequence>("aids", delimit(albumIds)));
  }

  public Document photos_getAlbums(Integer userId) throws FacebookException, IOException {
    return photos_getAlbums(userId, null);
  }

  public Document photos_getAlbums(Collection<Long> albumIds) throws FacebookException,
                                                                     IOException {
    return photos_getAlbums(null, albumIds);
  }

  /**
   * Retrieves the tags for the given set of photos.
   * @param photoIds The list of photos from which to extract photo tags.
   * @return an array of photo objects.
   */
  public Document photos_getTags(Collection<Long> photoIds) throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.PHOTOS_GET_TAGS,
                           new Pair<String, CharSequence>("pids", delimit(photoIds)));
  }

  /**
   * Retrieves the groups associated with a user
   * @param userId Optional: User associated with groups.
   *  A null parameter will default to the session user.
   * @param groupIds Optional: group ids to query.
   *   A null parameter will get all groups for the user.
   * @return array of groups
   */
  public Document groups_get(Integer userId, Collection<Long> groupIds) throws FacebookException,
                                                                               IOException {
    boolean hasGroups = (null != groupIds && !groupIds.isEmpty());
    if (null != userId)
      return hasGroups ?
             this.callMethod(FacebookMethod.GROUPS_GET, new Pair<String, CharSequence>("uid",
                                                                                       userId.toString()),
                             new Pair<String, CharSequence>("gids", delimit(groupIds))) :
             this.callMethod(FacebookMethod.GROUPS_GET,
                             new Pair<String, CharSequence>("uid", userId.toString()));
    else
      return hasGroups ?
             this.callMethod(FacebookMethod.GROUPS_GET, new Pair<String, CharSequence>("gids",
                                                                                       delimit(groupIds))) :
             this.callMethod(FacebookMethod.GROUPS_GET);
  }

  /**
   * Retrieves the membership list of a group
   * @param groupId : Group id
   * @return assoc array of four membership lists, with keys
   *  'members', 'admins', 'officers', and 'not_replied'
   */
  public Document groups_getMembers(Number groupId) throws FacebookException, IOException {
    assert (null != groupId);
    return this.callMethod(FacebookMethod.GROUPS_GET_MEMBERS,
                           new Pair<String, CharSequence>("gid", groupId.toString()));
  }

  /**
   * Retrieves the results of a Facebook Query Language query 
   * @param query : the FQL query statement
   * @return varies depending on the FQL query
   */
  public Document fql_query(CharSequence query) throws FacebookException, IOException {
    assert null != query;
    return this.callMethod(FacebookMethod.FQL_QUERY,
                           new Pair<String, CharSequence>("query", query));
  }

  /**
   * Retrieves the outstanding notifications for the session user.
   * @return assoc array of
   *  notification count pairs for 'messages', 'pokes' and 'shares',
   *  a uid list of 'friend_requests', a gid list of 'group_invites',
   *  and an eid list of 'event_invites'
   */
  public Document notifications_get() throws FacebookException, IOException {
    return this.callMethod(FacebookMethod.NOTIFICATIONS_GET);
  }

  /**
   * Call this function and store the result, using it to generate the
   * appropriate login url and then to retrieve the session information.
   * @return String the auth_token string
   */
  public String auth_createToken() throws FacebookException, IOException {
    Document d = this.callMethod(FacebookMethod.AUTH_CREATE_TOKEN);
    return d.getFirstChild().getTextContent();
  }

  /**
   * Call this function to retrieve the session information after your user has
   * logged in.
   * @param authToken the token returned by auth_createToken or passed back to your callback_url.
   */
  public String auth_getSession(String authToken) throws FacebookException, IOException {
    Document d =
      this.callMethod(FacebookMethod.AUTH_GET_SESSION, new Pair<String, CharSequence>("auth_token",
                                                                                      authToken.toString()));
    this._sessionKey =
        d.getElementsByTagName("session_key").item(0).getFirstChild().getTextContent();
    this._userId =
        Integer.parseInt(d.getElementsByTagName("uid").item(0).getFirstChild().getTextContent());
    if (this._isDesktop)
      this._sessionSecret =
          d.getElementsByTagName("secret").item(0).getFirstChild().getTextContent();
    return this._sessionKey;
  }
}
