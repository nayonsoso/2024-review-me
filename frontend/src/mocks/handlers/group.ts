import { http, HttpResponse } from 'msw';

import endPoint from '@/apis/endpoints';

import { CREATED_GROUP_DATA, INVALID_GROUP_ACCESS_CODE } from '../mockData/group';

// NOTE: URL 생성 정상 응답
const postDataForUrl = () => {
  return http.post(endPoint.postingDataForURL, async () => {
    return HttpResponse.json(CREATED_GROUP_DATA, { status: 200 });
  });
};

// NOTE: URL 생성 에러 응답
// const postDataForUrl = () => {
//   return http.post(endPoint.postingDataForURL, async () => {
//     return HttpResponse.json({ error: '서버 에러 테스트' }, { status: 500 });
//   });
// };

// NOTE: 확인 코드 정상 응답
const getIsValidGroupAccessCode = () => {
  return http.get(endPoint.gettingReviewList, async () => {
    return HttpResponse.json({ status: 200 });
  });
};

// NOTE: 확인 코드 에러 응답
// const getIsValidGroupAccessCode = () => {
//   return http.get(endPoint.gettingReviewList, async () => {
//     return HttpResponse.json(INVALID_GROUP_ACCESS_CODE, { status: 400 });
//   });
// };

// const getIsValidGroupAccessCode = () => {
//   return http.get(endPoint.gettingReviewList, async () => {
//     return HttpResponse.json({ error: '서버 에러 테스트' }, { status: 500 });
//   });
// };

const groupHandler = [postDataForUrl(), getIsValidGroupAccessCode()];

export default groupHandler;