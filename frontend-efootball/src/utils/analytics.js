/**
 * Umami 数据分析工具函数
 *
 * 使用方式：
 *   import { trackEvent, EVENTS } from '../utils/analytics';
 *   trackEvent(EVENTS.QUERY_PLAYER, { position: '中锋' });
 *
 * 注意：trackEvent 会安全地检查 window.umami 是否已加载，
 * 未加载时静默忽略，不会抛出错误。
 */

/**
 * 预定义事件名常量
 */
export const EVENTS = {
  /** 查询球员（筛选条件变化时触发） */
  QUERY_PLAYER: 'query_player',
  /** 翻页 */
  PAGE_CHANGE: 'page_change',
  /** 筛选：位置 */
  FILTER_POSITION: 'filter_position',
  /** 筛选：联赛 */
  FILTER_LEAGUE: 'filter_league',
  /** 筛选：俱乐部 */
  FILTER_CLUB: 'filter_club',
  /** 筛选：国家队 */
  FILTER_COUNTRY: 'filter_country',
  /** 筛选：惯用脚 */
  FILTER_FOOT: 'filter_foot',
  /** 筛选：现役状态 */
  FILTER_STATUS: 'filter_status',
  /** 筛选：身高 */
  FILTER_HEIGHT: 'filter_height',
  /** 重置筛选条件 */
  FILTER_RESET: 'filter_reset',
  /** 查看球员卡面图片 */
  VIEW_CARD_IMAGE: 'view_card_image',
  /** 访问管理页面 */
  MANAGE_PAGE_VISIT: 'manage_page_visit',
};

/**
 * 上报自定义事件到 Umami
 * @param {string} eventName - 事件名称，建议使用 EVENTS 常量
 * @param {Object} [eventData] - 事件附加数据（可选），会在 Umami 事件详情中展示
 */
export const trackEvent = (eventName, eventData) => {
  try {
    if (typeof window !== 'undefined' && window.umami && typeof window.umami.track === 'function') {
      window.umami.track(eventName, eventData);
    }
  } catch (e) {
    // 静默忽略，避免分析代码影响主业务
  }
};
