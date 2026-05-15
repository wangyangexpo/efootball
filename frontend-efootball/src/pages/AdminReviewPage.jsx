import React, { useState, useEffect, useCallback, useRef } from 'react';
import { Empty, Spin, message, Typography } from 'antd';
import ChangeRequestCard from '../components/ChangeRequestCard';
import {
  listChangeRequests,
  approveChangeRequest,
  rejectChangeRequest,
  extractData,
  extractError
} from '../api/changeRequest';

const { Title, Paragraph } = Typography;

const PAGE_SIZE = 12;

/**
 * 管理员审批页:列出 pending 提议,采纳/驳回后从列表中移除
 */
const AdminReviewPage = () => {
  const [items, setItems] = useState([]);
  const [pageNum, setPageNum] = useState(1);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [busyId, setBusyId] = useState(null);
  const sentinelRef = useRef(null);

  const fetchPage = useCallback(async (pn) => {
    setLoading(true);
    try {
      const resp = await listChangeRequests({
        pageNum: pn,
        pageSize: PAGE_SIZE,
        status: 'pending'
      });
      const data = extractData(resp);
      const list = data?.list || [];
      setItems(prev => pn === 1 ? list : [...prev, ...list]);
      setHasMore(pn < (data?.pages || 0));
    } catch (e) {
      message.error('加载提议列表失败');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPage(1);
  }, [fetchPage]);

  useEffect(() => {
    if (!hasMore || loading) return;
    const target = sentinelRef.current;
    if (!target) return;
    const observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting) {
        const next = pageNum + 1;
        setPageNum(next);
        fetchPage(next);
      }
    }, { rootMargin: '200px' });
    observer.observe(target);
    return () => observer.disconnect();
  }, [pageNum, hasMore, loading, fetchPage]);

  const removeFromList = (id) => {
    setItems(prev => prev.filter(it => it.id !== id));
  };

  const handleApprove = async (item) => {
    setBusyId(item.id);
    try {
      const resp = await approveChangeRequest(item.id);
      if (resp.data?.code === '200') {
        message.success(`已采纳并落库:${item.currentName}`);
        removeFromList(item.id);
      } else {
        message.error(extractError(resp) || '采纳失败');
      }
    } catch (e) {
      message.error('采纳失败:' + (e?.response?.data?.message || e.message));
    } finally {
      setBusyId(null);
    }
  };

  const handleReject = async (item, reason) => {
    setBusyId(item.id);
    try {
      const resp = await rejectChangeRequest(item.id, reason);
      if (resp.data?.code === '200') {
        message.success('已驳回');
        removeFromList(item.id);
      } else {
        message.error(extractError(resp) || '驳回失败');
      }
    } catch (e) {
      message.error('驳回失败:' + (e?.response?.data?.message || e.message));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div>
      <Typography style={{ marginBottom: 16 }}>
        <Title level={3} style={{ margin: 0 }}>修改提议审批</Title>
        <Paragraph type="secondary" style={{ margin: '4px 0 0' }}>
          仅管理员可见。采纳会立即覆盖球员对应字段并刷新缓存。
        </Paragraph>
      </Typography>

      {items.length === 0 && !loading ? (
        <Empty description="暂无待审批的修改提议" style={{ marginTop: 80 }} />
      ) : (
        <div className="cr-masonry">
          {items.map(item => (
            <ChangeRequestCard
              key={item.id}
              data={item}
              mode="review"
              busy={busyId === item.id}
              onApprove={() => handleApprove(item)}
              onReject={(reason) => handleReject(item, reason)}
            />
          ))}
        </div>
      )}

      <div ref={sentinelRef} style={{ height: 1 }} />
      {loading && (
        <div style={{ textAlign: 'center', padding: 24 }}>
          <Spin />
        </div>
      )}
      {!hasMore && items.length > 0 && (
        <div style={{ textAlign: 'center', padding: 16, color: '#aaa', fontSize: 12 }}>
          没有更多了
        </div>
      )}
    </div>
  );
};

export default AdminReviewPage;
