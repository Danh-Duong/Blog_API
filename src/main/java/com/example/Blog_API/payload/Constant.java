package com.example.Blog_API.payload;

import lombok.Data;

@Data
public class Constant {
    public static String GOOGLE_CLIENT_ID ="672564251488-nn8bqot6aiq2k96vrf8pn7ljqjgv19u3.apps.googleusercontent.com";
    public static String GOOGLE_CLIENT_SECRET="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";
    public static String GOOGLE_REDIRECT_URI="http://localhost:8080/LoginGoogle/LoginGoogleHandler";
    public static String GOOGLE_LINK_GET_TOKEN="https://accounts.google.com/o/oauth2/token";
    public static String GOOGLE_LINK_GET_USER_INFO="https://www.googleapis.com/oauth2/v1/userinfo?access_token=";
}
