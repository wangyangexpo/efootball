import React, { useState, useEffect, useCallback } from 'react';
import { FloatButton, Popover, message } from 'antd';
import { MessageOutlined } from '@ant-design/icons';
import FilterPanel from '../components/FilterPanel';
import PlayerTable from '../components/PlayerTable';
import { getPlayers, extractData } from '../api/player';
import qrcodeImage from '../assets/douyin.jpeg';

const HomePage = () => {
  const [filters, setFilters] = useState({
    position: undefined,
    status: undefined,
    number: undefined,
    league: undefined,
    club: undefined,
    country: undefined,
    foot: undefined,
    height: undefined,
    heightOperator: '='
  });
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 20, total: 0 });

  const loadPlayers = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        ...filters,
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      };
      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === null) {
          delete params[key];
        }
      });

      const response = await getPlayers(params);
      const data = extractData(response);
      setPlayers(data.list || []);
      setPagination(prev => ({ ...prev, total: data.total }));
    } catch (error) {
      message.error('加载球员数据失败');
    }
    setLoading(false);
  }, [filters, pagination.current, pagination.pageSize]);

  useEffect(() => {
    loadPlayers();
  }, [loadPlayers]);

  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
    setPagination(prev => ({ ...prev, current: 1 }));
  };

  const handlePageChange = (newPagination) => {
    setPagination({
      current: newPagination.current,
      pageSize: newPagination.pageSize,
      total: pagination.total
    });
  };

  const feedbackContent = (
    <div style={{ textAlign: 'center' }}>
      <img
        src={qrcodeImage}
        alt="反馈二维码"
        style={{ width: 200, height: 'auto', marginBottom: 8 }}
      />
      <div style={{ fontSize: 12, color: '#666' }}>
        反馈球员bug或者数据错误，请加抖音
      </div>
    </div>
  );

  return (
    <>
      <FilterPanel
        onFilterChange={handleFilterChange}
        total={pagination.total}
      />
      <PlayerTable
        players={players}
        loading={loading}
        pagination={pagination}
        onPageChange={handlePageChange}
      />
      <Popover
        content={feedbackContent}
        title="问题反馈"
        trigger="hover"
        placement="leftBottom"
      >
        <FloatButton
          icon={<MessageOutlined />}
          type="primary"
          style={{ right: 24, bottom: 24 }}
          tooltip="反馈问题"
        />
      </Popover>
    </>
  );
};

export default HomePage;