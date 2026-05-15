import axios from 'axios';

// 公开接口 axios 实例(无鉴权)
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
});

// 管理员接口 axios 实例:每次请求自动从 sessionStorage 读 MD5 写到请求头
const adminApi = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000
});

// 与后端 AdminAuthInterceptor 约定:前端 sessionStorage('adminPasswordMd5') 存 MD5
// PasswordModal 校验通过时会写入这个值
adminApi.interceptors.request.use((config) => {
  const pwd = sessionStorage.getItem('adminPasswordMd5');
  if (pwd) {
    config.headers['X-Admin-Password'] = pwd;
  }
  return config;
});

// ----- 公开接口 -----

// 提交修改提议
export const submitChangeRequest = (data) =>
  api.post('/change-request', data);

// 列表
export const listChangeRequests = (params) =>
  api.get('/change-request', { params });

// 详情
export const getChangeRequest = (id, voterId) =>
  api.get(`/change-request/${id}`, { params: { voterId } });

// 投票
export const voteChangeRequest = (id, voterId, voteType) =>
  api.post(`/change-request/${id}/vote`, { voterId, voteType });

// ----- 管理员接口 -----

export const approveChangeRequest = (id, reason) =>
  adminApi.post(`/admin/change-request/${id}/approve`, { reason });

export const rejectChangeRequest = (id, reason) =>
  adminApi.post(`/admin/change-request/${id}/reject`, { reason });

// 响应数据提取辅助
export const extractData = (response) => response.data?.data;
export const extractError = (response) => response.data?.message;
