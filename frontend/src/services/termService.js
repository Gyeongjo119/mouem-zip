// src/services/termService.js

// 예시용 데이터 — 실제로는 API 호출로 대체 가능
export async function fetchTerms() {
  return [
    {
      term_id: 1,
      term_name: '예시 용어',
      term_define: '이것은 예시입니다.',
      category_name: '예시 카테고리',
      category_color: '#d1e7dd',
    },
    {
      term_id: 2,
      term_name: '보험',
      term_define: '사고 시 보장해주는 계약',
      category_name: '금융',
      category_color: '#cfe2ff',
    },
  ];
}
