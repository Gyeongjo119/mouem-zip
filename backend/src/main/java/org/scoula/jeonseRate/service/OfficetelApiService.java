package org.scoula.jeonseRate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.scoula.jeonseRate.dto.DealResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 오피스텔 실거래가 데이터를 국토교통부 Open API를 통해 조회하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class OfficetelApiService {

    // 공공데이터 포털 API 호출용 WebClient (baseUrl은 WebClientConfig에서 설정)
    private final WebClient webClient;
    // JSON 응답을 DealResponseDTO로 파싱하기 위한 Jackson ObjectMapper
    private final ObjectMapper objectMapper;

    @Value("${openapi.key}")
    private String apiKey;

    @Value("${openapi.office.url}")
    private String officeApiUrl;

    /**
     * 오피스텔 실거래가 조회
     * @param lawdCode 법정동 코드 (5자리)
     * @param dealYmd  거래 연월 (예: "202406")
     * @return 파싱된 거래 응답 DTO
     */
    public DealResponseDTO getOfficeDeals(String lawdCode, String dealYmd) {
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(officeApiUrl.replace("https://apis.data.go.kr", ""))  // path만 추출
                        .queryParam("serviceKey", apiKey)       // 인증키
                        .queryParam("LAWD_CD", lawdCode)        // 법정동 코드
                        .queryParam("DEAL_YMD", dealYmd)        // 거래 연월
                        .queryParam("_type", "json")     // 응답 형식
                        .queryParam("numOfRows", "1000") // 최대 1000건까지 요청
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("📦 [API 호출 완료] 유형: 아파트/연립/오피스텔, 응답 길이: " + response.length());

        try {
            return objectMapper.readValue(response, DealResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("오피스텔 실거래가 응답 파싱 실패", e);
        }
    }
}
