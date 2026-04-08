import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
});

const MANAGE_PASSWORD = 'wangyangexpo';

// 获取枚举
export const getPlayerEnums = () => api.get('/player/enums');

// 分页查询球员
export const getPlayers = (params) => api.get('/player', { params });

// 查询球员详情
export const getPlayerById = (id) => api.get(`/player/${id}`);

// 新增球员
export const createPlayer = (data) => api.post('/player', {
  ...data,
  password: MANAGE_PASSWORD
});

// 修改球员
export const updatePlayer = (id, data) => api.post(`/player/${id}`, {
  ...data,
  password: MANAGE_PASSWORD
});

// 删除球员
export const deletePlayer = (id) => api.post(`/player/delete/${id}`, {
  password: MANAGE_PASSWORD
});

// 响应数据提取辅助函数
export const extractData = (response) => response.data.data;

export default api;