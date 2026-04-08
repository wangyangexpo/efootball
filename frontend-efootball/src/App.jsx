import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout, Typography, Space, ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import HomePage from './pages/HomePage';
import PlayerManage from './pages/PlayerManage';
import PasswordModal from './components/PasswordModal';
import './App.css';

const { Header, Content } = Layout;
const { Title } = Typography;

// 管理页面路由守卫
const ManageRoute = () => {
  const isAuthenticated = sessionStorage.getItem('playerManageAuth') === 'true';
  return isAuthenticated ? <PlayerManage /> : <PasswordModal />;
};

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
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
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/manage" element={<ManageRoute />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Content>
        </Layout>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;