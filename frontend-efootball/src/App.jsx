import React, { useState, useEffect, useCallback } from 'react';
import { Layout, Typography, Space, Button, FloatButton, Popover, message } from 'antd';
import { MessageOutlined, SearchOutlined, SettingOutlined } from '@ant-design/icons';
import FilterPanel from './components/FilterPanel';
import PlayerTable from './components/PlayerTable';
import PasswordModal from './components/PasswordModal';
import PlayerManage from './pages/PlayerManage';
import { getPlayers, extractData } from './api/player';
import qrcodeImage from './assets/douyin.jpeg';
import './App.css';

const { Header, Content } = Layout;
const { Title } = Typography;

function App() {
  const [currentPage, setCurrentPage] = useState('main');
  const [isAuthenticated, setIsAuthenticated] = useState(
    sessionStorage.getItem('playerManageAuth') === 'true'
  );

  // 主页面状态
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

  // 加载球员数据
  const loadPlayers = useCallback(async () => {
    setLoading(true);
    try {
      const params = {
        ...filters,
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      };
      // 移除 undefined 值
      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === null) {
          delete params[key];
        }
      });

      const response = await getPlayers(params);
      const data = extractData(response);
      setPlayers(data.records || []);
      setPagination(prev => ({ ...prev, total: data.total }));
    } catch (error) {
      message.error('加载球员数据失败');
    }
    setLoading(false);
  }, [filters, pagination.current, pagination.pageSize]);

  useEffect(() => {
    if (currentPage === 'main') {
      loadPlayers();
    }
  }, [currentPage, loadPlayers]);

  // 筛选条件变化
  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
    setPagination(prev => ({ ...prev, current: 1 })); // 重置到第一页
  };

  // 分页变化
  const handlePageChange = (newPagination) => {
    setPagination({
      current: newPagination.current,
      pageSize: newPagination.pageSize,
      total: pagination.total
    });
  };

  // 页面切换
  const handlePageSwitch = (page) => {
    setCurrentPage(page);
  };

  // 密码验证成功
  const handleAuthSuccess = () => {
    setIsAuthenticated(true);
  };

  // 返回主页面
  const handleBackToMain = () => {
    setCurrentPage('main');
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
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        padding: '0 50px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between'
      }}>
        <Space>
          <Title level={3} style={{ color: '#fff', margin: 0 }}>
            ⚽ 实况足球球员筛选系统
          </Title>
        </Space>
        <Space>
          <Button
            type={currentPage === 'main' ? 'primary' : 'default'}
            icon={<SearchOutlined />}
            onClick={() => handlePageSwitch('main')}
          >
            球员查询
          </Button>
          <Button
            type={currentPage === 'manage' ? 'primary' : 'default'}
            icon={<SettingOutlined />}
            onClick={() => handlePageSwitch('manage')}
          >
            球员管理
          </Button>
        </Space>
      </Header>

      <Content style={{ padding: '24px 50px' }}>
        {currentPage === 'main' && (
          <>
            <FilterPanel
              filters={filters}
              onFilterChange={handleFilterChange}
            />
            <PlayerTable
              players={players}
              loading={loading}
              pagination={pagination}
              onPageChange={handlePageChange}
            />
          </>
        )}

        {currentPage === 'manage' && (
          isAuthenticated ? (
            <PlayerManage />
          ) : (
            <PasswordModal
              onSuccess={handleAuthSuccess}
              onCancel={handleBackToMain}
            />
          )
        )}
      </Content>

      {currentPage === 'main' && (
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
      )}
    </Layout>
  );
}

export default App;