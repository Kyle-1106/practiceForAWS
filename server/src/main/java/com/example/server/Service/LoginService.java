package com.example.server.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.server.dto.response.LoginResponse;
import com.example.server.entity.User;
import com.example.server.mapper.LoginMapper;
import java.util.Date;

@Service
public class LoginService {
    // 有効期限を1時間に設定
    private static final long EXPIRATION_TIME = 1000L * 60L * 60L * 1L;
    private static final String SECRET_KEY = "jwt";
    private static final String ISSUER = "your-application";
    private static final String SUBJECT = "authentication";

    @Autowired
    private LoginMapper loginMapper;

    public LoginResponse authenticate(String email, String password) {
      try {
        User user = loginMapper.findByEmail(email);
        System.out.println(user);  
        if (user == null) {
            throw new RuntimeException("ユーザーが見つかりません");
        }

        System.out.println(user.getPassword());
        System.out.println(password);
        // パスワードの前後の空白を除去して比較
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("パスワードが一致しません");
        }

        String token = generateJWT(user);
        return new LoginResponse(token, user);
        
      } catch (Exception e) {
        throw new RuntimeException("ログインに失敗しました");
      }
    }

    private String generateJWT(User user) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + EXPIRATION_TIME);
        
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(SUBJECT)
                .withAudience(user.getEmail())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }
}
