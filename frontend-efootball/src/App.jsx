import React, { useState, useMemo } from 'react';
import { Layout, Typography, Space, FloatButton, Popover } from 'antd';
import { MessageOutlined } from '@ant-design/icons';
import FilterPanel from './components/FilterPanel';
import PlayerTable from './components/PlayerTable';
import playersData from './data/players.json';
import qrcodeImage from './assets/douyin.jpeg';
import './App.css';

const { Header, Content } = Layout;
const { Title } = Typography;

function App() {
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

  // 筛选逻辑
  const filteredPlayers = useMemo(() => {
    let result = playersData.players;

    // 球员位置筛选
    if (filters.position) {
      result = result.filter(player => player.position === filters.position);
    }

    // 现役状态筛选
    if (filters.status) {
      result = result.filter(player => player.status === filters.status);
    }

    // 球衣号码筛选
    if (filters.number !== undefined && filters.number !== null) {
      result = result.filter(player => player.number === filters.number);
    }

    // 联赛筛选
    if (filters.league) {
      result = result.filter(player => player.league === filters.league);
    }

    // 俱乐部筛选
    if (filters.club) {
      result = result.filter(player => player.club === filters.club);
    }

    // 国家队筛选
    if (filters.country) {
      result = result.filter(player => player.country === filters.country);
    }

    // 惯用脚筛选
    if (filters.foot) {
      result = result.filter(player => player.foot === filters.foot);
    }

    // 身高筛选
    if (filters.height) {
      const height = filters.height;
      const operator = filters.heightOperator;

      if (operator === '=') {
        result = result.filter(player => player.height === height);
      } else if (operator === '+') {
        result = result.filter(player => player.height > height);
      } else if (operator === '-') {
        result = result.filter(player => player.height < height);
      }
    }

    return result;
  }, [filters]);

  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
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
        alignItems: 'center'
      }}>
        <Space>
          <Title level={3} style={{ color: '#fff', margin: 0 }}>
            ⚽ 实况足球球员筛选系统
          </Title>
        </Space>
      </Header>
      <Content style={{ padding: '24px 50px' }}>
        <FilterPanel 
          filters={filters}
          onFilterChange={handleFilterChange}
          enums={playersData.enums}
        />
        <PlayerTable 
          players={filteredPlayers}
          loading={false}
        />
      </Content>
      
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
    </Layout>
  );
}

export default App;
