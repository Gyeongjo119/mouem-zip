package org.scoula.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.register.domain.dto.MortgageDTO;
import org.scoula.register.util.RegisterUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class MortgageServiceImpl implements MortgageService {

    // 등기 데이터 중 근저당권 정보만 추출
    @Override
    public List<MortgageDTO> extractMortgageInfos(List<List<String>> tableData) {
        List<MortgageDTO> mortgages = new ArrayList<>();
        List<String> canceledRanks = new ArrayList<>();
        List<List<String>> mergedTable = RegisterUtils.mergeRowsWithCanceled(tableData);
        // 말소된 등기 찾기
        for (List<String> row : mergedTable) {
            if (row.size() < 2) continue;

            String registrationPurpose = RegisterUtils.normalizeText(row.get(1));

            if (registrationPurpose != null && registrationPurpose.contains("근저당권") && registrationPurpose.contains("말소")) {
//                System.out.println("읽어온 문장: " + registrationPurpose);
                List<String> canceled = RegisterUtils.extractCanceledRanks(registrationPurpose);  // 말소된 번호
                canceledRanks.addAll(canceled);
            }
        }

        // 본 등기에서 말소된 순번은 제외하고 처리
        for (List<String> row : mergedTable) {
            if (row.size() < 5) continue;

            String rank = row.get(0).trim();    // 순위번호
            String registrationPurpose = RegisterUtils.normalizeText(row.get(1));    // 등기목적

            if (registrationPurpose != null &&
                    registrationPurpose.contains("근저당권") &&
                    !registrationPurpose.contains("말소")) {

                String date = RegisterUtils.extractDate(RegisterUtils.normalizeText(row.get(2)));  // 접수일자
                String etc = row.get(4);                // 권리자 및 기타사항

                String maxClaimAmount = RegisterUtils.extractAmountByKeywords(etc);
                String mortgageHolder = RegisterUtils.extractCreditorOrRightHolder(etc);

                MortgageDTO info = new MortgageDTO();
                info.setRank(rank);
                info.setDate(date);
                info.setMaxClaimAmount(maxClaimAmount);
                info.setMortgageHolder(mortgageHolder);
                info.setCanceled(canceledRanks.contains(rank));

                mortgages.add(info);
            }
        }

        return mortgages;
    }
}
