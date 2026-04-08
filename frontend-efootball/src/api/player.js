import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
});

// 密码的 MD5 值
const PASSWORD_MD5 = '6b09e658e9143361008d26983cc738ec';

// 获取枚举
export const getPlayerEnums = () => api.get('/player/enums');

// 分页查询球员
export const getPlayers = (params) => api.get('/player', { params });

// 新增球员
export const createPlayer = (data) => api.post('/player', {
  ...data,
  password: PASSWORD_MD5
});

// 修改球员
export const updatePlayer = (id, data) => api.post(`/player/${id}`, {
  ...data,
  password: PASSWORD_MD5
});

// 删除球员
export const deletePlayer = (id) => api.post(`/player/delete/${id}`, {
  password: PASSWORD_MD5
});

// 响应数据提取辅助函数
export const extractData = (response) => response.data.data;

export default api;