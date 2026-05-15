import React, { useState, useEffect, useCallback, useRef } from 'react';
import { Empty, Spin, message, Typography } from 'antd';
import ChangeRequestCard from '../components/ChangeRequestCard';
import { listChangeRequests, voteChangeRequest, extractData, extractError } from '../api/changeRequest';
import { getVisitorId } from '../utils/visitorId';

const { Title, Paragraph } = Typography;

const PAGE_SIZE = 12;

/**
 * 公开瀑布流页面:展示所有 pending 修改提议,任何访客可投票
 */
const ChangeRequestsPage = () => {
  const [items, setItems] = useState([]);
  const [pageNum, setPageNum] = useState(1);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [busyId, setBusyId] = useState(null);
  const sentinelRef = useRef(null);
  const visitorId = useRef(getVisitorId()).current;

  const fetchPage = useCallback(async (pn) => {
    setLoading(true);
    try {
      const resp = await listChangeRequests({
        pageNum: pn,
        pageSize: PAGE_SIZE,
        status: 'pending',
        voterId: visitorId
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
  }, [visitorId]);

  // 初始加载
  useEffect(() => {
    fetchPage(1);
  }, [fetchPage]);

  // 触底加载下一页
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

  const handleVote = async (item, voteType) => {
    setBusyId(item.id);
    try {
      const resp = await voteChangeRequest(item.id, visitorId, voteType);
      if (resp.data?.code === '200') {
        const updated = extractData(resp);
        setItems(prev => {
          const next = prev.map(it => it.id === updated.id ? updated : it);
          // 重新按 approve_count 降序、id 降序排序(本地排序保持与后端一致)
          next.sort((a, b) => {
            if (b.approveCount !== a.approveCount) return b.approveCount - a.approveCount;
            return b.id - a.id;
          });
          return next;
        });
      } else {
        message.error(extractError(resp) || '投票失败');
      }
    } catch (e) {
      message.error('投票失败:' + (e?.response?.data?.message || e.message));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div>
      <Typography style={{ marginBottom: 16 }}>
        <Title level={3} style={{ margin: 0 }}>球员数据修改提议</Title>
        <Paragraph type="secondary" style={{ margin: '4px 0 0' }}>
          点击卡片翻转查看改动详情。赞同数高的提议会优先展示;管理员审批后落库。
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
              mode="public"
              busy={busyId === item.id}
              onVote={(voteType) => handleVote(item, voteType)}
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

export default ChangeRequestsPage;
