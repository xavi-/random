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

import java.util.EnumSet;

public enum FacebookMethod
  implements CharSequence {
  // Authentication
  AUTH_CREATE_TOKEN("facebook.auth.createToken"),
  AUTH_GET_SESSION("facebook.auth.getSession", 1),
  // FQL Query
  FQL_QUERY("facebook.fql.query",1),
  // Events
  EVENTS_GET("facebook.events.get", 5),
  EVENTS_GET_MEMBERS("facebook.events.getMembers", 1),
  // Friends
  FRIENDS_GET("facebook.friends.get"),
  FRIENDS_GET_APP_USERS("facebook.friends.getAppUsers"),
  FRIENDS_GET_REQUESTS("facebook.friends.getRequests"),
  FRIENDS_ARE_FRIENDS("facebook.friends.areFriends", 2),
  // Users
  USERS_GET_INFO("facebook.users.getInfo", 2),
  USERS_GET_LOGGED_IN_USER("facebook.users.getLoggedInUser", 1),
  // Photos
  PHOTOS_GET("facebook.photos.get", 2),
  PHOTOS_GET_ALBUMS("facebook.photos.getAlbums", 1),
  PHOTOS_GET_TAGS("facebook.photos.getTags", 1),
  // Notifications
  NOTIFICATIONS_GET("facebook.notifications.get"),
  // Groups
  GROUPS_GET("facebook.groups.get", 1),
  GROUPS_GET_MEMBERS("facebook.groups.getMembers", 1),
  ;

  private String methodName;
  private int numParams;
  private int maxParamsWithSession;

  private static EnumSet<FacebookMethod> preAuth = null;
  private static EnumSet<FacebookMethod> postAuth = null;

  public static EnumSet<FacebookMethod> preAuthMethods() {
    if (null == preAuth)
      preAuth = EnumSet.of(AUTH_CREATE_TOKEN, AUTH_GET_SESSION);
    return preAuth;
  }

  public static EnumSet<FacebookMethod> postAuthMethods() {
    if (null == postAuth)
      postAuth = EnumSet.complementOf(preAuthMethods());
    return postAuth;
  }

  FacebookMethod(String name) {
    this(name, 0);
  }

  FacebookMethod(String name, int maxParams) {
    assert (name != null && 0 != name.length());
    this.methodName = name;
    this.numParams = maxParams;
    this.maxParamsWithSession = maxParams + FacebookRestClient.NUM_AUTOAPPENDED_PARAMS;
  }

  public String methodName() {
    return this.methodName;
  }

  public int numParams() {
    return this.numParams;
  }

  public boolean requiresSession() {
    return postAuthMethods().contains(this);
  }

  public int numTotalParams() {
    return requiresSession() ? this.maxParamsWithSession : this.numParams;
  }

  /* Implementing CharSequence */

  public char charAt(int index) {
    return this.methodName.charAt(index);
  }

  public int length() {
    return this.methodName.length();
  }

  public CharSequence subSequence(int start, int end) {
    return this.methodName.subSequence(start, end);
  }

  public String toString() {
    return this.methodName;
  }
}
