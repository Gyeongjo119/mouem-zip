<script setup>
import Header from '../components/Header.vue'
import TermModal from "@/components/TermModal.vue";
import TermCard from "@/components/TermCard.vue";
</script>

<template>
  <div class="term-view container mt-5">
    <!-- 검색창 -->
    <div class="row justify-content-center mb-3">
      <div class="col-md-8 d-flex justify-content-center">
        <input
            type="text"
            v-model="keyword"
            class="form-control rounded-pill px-4"
            placeholder="검색할 용어를 입력하세요"
            style="width: 66%;"
        />
      </div>
    </div>

    <!-- 카테고리 필터 (오른쪽 정렬) -->
    <div class="row mb-4">
      <div class="col text-end">
        <select v-model="filter" class="form-select w-auto d-inline-block" style="margin-right: 11rem;">
          <option value="">전체</option>
          <option v-for="cat in categories" :key="cat" :value="cat">
            {{ cat }}
          </option>
        </select>
      </div>
    </div>

    <!-- 카드 리스트 -->
    <div class="row justify-content-center g-4">
      <div
          class="col-12 col-sm-6 col-md-4"
          v-for="item in filteredTerms"
          :key="item.id"
          style="max-width: 380px;"
      >
        <TermCard :term="item" @open="openModal" />
      </div>
    </div>

    <!-- 모달 -->
    <TermModal :term="selectedTerm" @close="selectedTerm = null" />
  </div>
</template>

<script>
import TermCard from '@/components/TermCard.vue'
import TermModal from '@/components/TermModal.vue'

export default {
  name: 'TermView',
  components: { TermCard, TermModal },
  data() {
    return {
      keyword: '',
      filter: '',
      selectedTerm: null,
      // 예시데이터 삽입
      terms: [
        {
          id: 1,
          title: '가압류/가처분',
          category: '전세계약과 돈 관련',
          definition: '상대방 재산을 미리 묶어두는 법적 조치예요. 등기부에 뜨면 위험 신호예요. 보증보험 가입 거절될 수도 있어요.',
          warning: '보증금보다 근저당이 많으면 보증금을 돌려받기 어려울 수 있습니다.',
          example: 'KB국민은행 근저당권 2억 설정',
          color: 'rgba(183, 255, 172, 0.45)'
        },
        {
          id: 2,
          title: '근저당권',
          category: '전세계약과 돈 관련',
          definition: '근저당권은 집주인이 금융기관에서 돈을 빌릴 때 설정하는 담보 권리입니다.',
          warning: '전세보증금보다 근저당권이 많을 경우, 보증금을 돌려받지 못할 위험이 있습니다.',
          example: '등기부등본 상 근저당권이 3억으로 설정된 경우',
          color: 'rgba(183, 255, 172, 0.45)'
        },
        {
          id: 3,
          title: '전입신고',
          category: '전세권 보호',
          definition: '전입신고를 해야 주민등록과 확정일자를 통해 우선변제를 받을 수 있어요.',
          warning: '전입신고 없이 확정일자만 받아도 전세보증금은 보호받지 못할 수 있어요.',
          example: '입주 후 14일 이내 동주민센터 방문 또는 정부24에서 온라인 신고',
          color: 'rgba(255,102,102,0.22)'
        },
        {
          id: 4,
          title: '확정일자',
          category: '전세권 보호',
          definition: '확정일자는 임대차계약서에 공적인 날짜를 부여해서 우선변제를 받을 수 있게 해주는 제도예요.',
          warning: '전입신고만 하고 확정일자 안 받으면 전세보증금이 완전히 보호되지 않을 수 있어요.',
          example: '동주민센터 방문 후 임대차계약서에 확정일자 도장 받기',
          color: 'rgba(255,102,102,0.22)'
        },
        {
          id: 5,
          title: '중도금',
          category: '전세계약과 돈 관련',
          definition: '중도금은 전세계약 시 계약금과 잔금 사이에 지급하는 금액으로, 일반적으로 전체 금액의 일부를 나눠서 지급해요.',
          warning: '중도금을 지급할 때 등기부등본 상태를 한 번 더 확인하는 게 안전해요.',
          example: '계약 후 2주 이내 중도금 1,000만원 송금',
          color: 'rgba(183, 255, 172, 0.45)'
        },
        {
          id: 6,
          title: '집주인 변경 통보',
          category: '전세 계약 기간 및 갱신',
          definition: '임대인(집주인)이 계약 중간에 바뀌면 기존 세입자에게 법적으로 통보해야 하고, 세입자는 변경 사실을 알고 있어야 권리 주장 가능해요.',
          warning: '집주인 변경을 모른 채 있다가 보증금을 못 돌려받는 경우도 있어요.',
          example: '매매로 집주인 바뀔 때 등기부등본 확인 및 통보 확인',
          color: 'rgba(255,245,28,0.45)'
        },
        {
          id: 7,
          title: '계약갱신요구권',
          category: '전세 계약 기간 및 갱신',
          definition: '계약갱신요구권은 세입자가 1회에 한해 기존 계약을 2년 더 연장할 수 있는 권리예요. 임대인은 특별한 사유 없이는 거절할 수 없어요.',
          warning: '계약 만료 6개월~2개월 전 사이에 서면 통보하지 않으면 행사 불가능해요.',
          example: '계약 종료일 5개월 전 집주인에게 서면으로 연장 의사 전달',
          color: 'rgba(255,245,28,0.45)'
        }
      ]
    }
  },
  computed: {
    filteredTerms() {
      return this.terms.filter(term =>
          (!this.filter || term.category === this.filter) &&
          term.title.includes(this.keyword)
      )
    },
    categories() {
      // terms 배열에 있는 category들을 중복 없이 뽑아냄
      const allCategories = this.terms.map(term => term.category)
      return [...new Set(allCategories)] // Set으로 중복 제거
    }
  },
  methods: {
    openModal(term) {
      this.selectedTerm = term
    }
  }
}
</script>

<style scoped>
.term-view {
  padding-bottom: 40px;
}
</style>