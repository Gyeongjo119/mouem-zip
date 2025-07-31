package org.scoula.oauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.scoula.oauth.domain.DTO.KakaoApiResponse;
import org.scoula.oauth.domain.DTO.KakaoTokenResponseDTO;
import org.scoula.oauth.domain.DTO.KakaoUserDTO;
import org.scoula.oauth.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class KakaoOAuthServiceImpl implements KakaoOAuthService {

    private final JwtProcessor jwtProcessor;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserMapper userMapper;

    @Override
    public String getAccessTokenFromKakao(String code) {
        try {
            URL url = new URL("https://kauth.kakao.com/oauth/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // POST 요청 본문 작성 가능하게 함

            // 요청 바디 작성
            String body = "grant_type=authorization_code"
                    + "&client_id=88a530611ac6fa5a18f5747f67b6a359"
                    + "&redirect_uri=http://localhost:8080/oauth/callback/kakao"
                    + "&code=" + code;

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            bw.write(body);
            bw.flush();
            bw.close();


            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            String result = sb.toString();
            System.out.println("[카카오 토큰 응답] " + result); // JSON 전체 응답 출력

            // JSON 파싱해서 access_token 추출
            KakaoTokenResponseDTO tokenResponse = objectMapper.readValue(result, KakaoTokenResponseDTO.class);
            return tokenResponse.getAccess_token();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 토큰 요청 실패", e);
        }
    }

    @Override
    public String loginWithKakao(String kakaoAccessToken) {
        // 카카오 사용자 정보 가져오기
        KakaoUserDTO kakaoUser = requestKakaoUserInfo(kakaoAccessToken);

        // 우리 DB에 사용자 있는지 확인 (없으면 저장)
        KakaoUserDTO user = userService.loginOrRegister(kakaoUser);

        // JWT 발급
        return jwtProcessor.generateToken(user.getKakaoId());
    }

    @Override
    public KakaoUserDTO requestKakaoUserInfo(String token) {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";

        try {
            // 액세스 토큰을 이용해 사용자 정보 요청
            HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            // 로그 확인용
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responses = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responses.append(line);
            }
            String json = responses.toString();
            System.out.println("[카카오 응답] " + json); // 👈 추가

            // 요청 받은 json DTO 형식으로 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            KakaoApiResponse response = objectMapper.readValue(json, KakaoApiResponse.class);


            String kakaoId = response.getId();
            String name = response.getProperties().getNickname();
            String email = response.getKakao_account().getEmail();
            return new KakaoUserDTO(kakaoId, name, email, true);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("사용자 정보 요청 실패", e);
        }
    }

    @Override
    public void softDeleteUser(String kakaoId) {
        userMapper.softDeleteUser(kakaoId);
    }

    public void unlinkKakaoAccount(String accessToken) {
        try {
            URL url = new URL("https://kapi.kakao.com/v1/user/unlink");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("[회원탈퇴 성공]");
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println("[회원탈퇴 실패] " + line);
                }
                br.close();
            }

        } catch (IOException e) {
            throw new RuntimeException("카카오 회원 탈퇴 요청 실패", e);
        }
    }

}
