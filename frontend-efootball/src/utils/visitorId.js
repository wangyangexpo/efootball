// 匿名访客 ID:首次访问生成 UUID 写入 localStorage,后续读取
const KEY = 'visitorId';

const fallbackUuid = () => {
  // crypto.randomUUID 在旧浏览器/非安全上下文下不可用,提供一个简易兜底
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
};

export const getVisitorId = () => {
  let id = localStorage.getItem(KEY);
  if (!id) {
    id = (window.crypto && typeof window.crypto.randomUUID === 'function')
      ? window.crypto.randomUUID()
      : fallbackUuid();
    localStorage.setItem(KEY, id);
  }
  return id;
};
