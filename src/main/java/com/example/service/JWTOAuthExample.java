package com.example.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.coze.openapi.client.auth.OAuthToken;
import com.coze.openapi.service.auth.JWTOAuth;
import com.coze.openapi.service.auth.JWTOAuthClient;
import com.coze.openapi.service.service.CozeAPI;

/*
This example is about how to use the service jwt oauth process to acquire user authorization.

Firstly, users need to access https://www.coze.com/open/oauth/apps. For the cn environment,
users need to access https://www.coze.cn/open/oauth/apps to create an OAuth App of the type
of Service application.
The specific creation process can be referred to in the document:
https://www.coze.com/docs/developer_guides/oauth_jwt. For the cn environment, it can be
accessed at https://www.coze.cn/docs/developer_guides/oauth_jwt.
After the creation is completed, the client ID, private key, and public key id, can be obtained.
For the client secret and public key id, users need to keep it securely to avoid leakage.
* */
public class JWTOAuthExample {

    public static void main(String[] args) {


        // The default access is api.coze.com, but if you need to access api.coze.cn,
        // please use base_url to configure the api endpoint to access

        String cozeAPIBase = "api.coze.cn";

        // ✅ 2. 填写你的 OAuth 应用信息（从 Coze 开发者平台获取）
        String jwtOauthClientID = "1114586840596";  // <-- 替换为你的实际 client_id
        String jwtOauthPublicKeyID = "5VCjKuGWMd9gF7VYzsQ8lub1akIYRRe8YIDikZI4ToU";  // <-- 替换为你的实际 public key ID
        String jwtOauthPrivateKeyFilePath = "src/main/java/com/example/OAuthkey.pem";  // <-- 替换为你的私钥文件路径
        String jwtOauthPrivateKey = null;
        try {
            jwtOauthPrivateKey = new String(
                    Files.readAllBytes(Paths.get(jwtOauthPrivateKeyFilePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("读取私钥文件失败，请检查路径是否正确！");
            e.printStackTrace();
            return;
        }
//        String jwtOauthClientID = System.getenv("COZE_JWT_OAUTH_CLIENT_ID");
//        String jwtOauthPrivateKey = System.getenv("COZE_JWT_OAUTH_PRIVATE_KEY");
//        String jwtOauthPrivateKeyFilePath = System.getenv("COZE_JWT_OAUTH_PRIVATE_KEY_FILE_PATH");
//        String jwtOauthPublicKeyID = System.getenv("COZE_JWT_OAUTH_PUBLIC_KEY_ID");
        //    jwtOauthPublicKeyID+="123";
        JWTOAuthClient oauth = null;


    /*
    The jwt oauth type requires using private to be able to issue a jwt token, and through
    the jwt token, apply for an access_token from the coze service. The sdk encapsulates
    this procedure, and only needs to use get_access_token to obtain the access_token under
    the jwt oauth process.
    Generate the authorization token
    The default ttl is 900s, and developers can customize the expiration time, which can be
    set up to 24 hours at most.
    * */
        try {
            oauth =
                    new JWTOAuthClient.JWTOAuthBuilder()
                            .clientID(jwtOauthClientID)
                            .privateKey(jwtOauthPrivateKey)
                            .publicKey(jwtOauthPublicKeyID)
                            .baseURL(cozeAPIBase)
                            .build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            OAuthToken resp = oauth.getAccessToken();
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    /*
    The jwt oauth process does not support refreshing tokens. When the token expires,
    just directly call get_access_token to generate a new token.
    * */
        CozeAPI coze =
                new CozeAPI.Builder()
                        .auth(JWTOAuth.builder().jwtClient(oauth).build())
                        .baseURL(cozeAPIBase)
                        .build();
        // you can also specify the scope and session for it
    }
}