import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, NavLink } from 'react-router-dom';
import { Layout, Typography, Space, ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import HomePage from './pages/HomePage';
import PlayerManage from './pages/PlayerManage';
import ChangeRequestsPage from './pages/ChangeRequestsPage';
import AdminReviewPage from './pages/AdminReviewPage';
import PasswordModal from './components/PasswordModal';
import './App.css';

const { Header, Content } = Layout;
const { Title } = Typography;

// 管理页面路由守卫(/manage 与 /admin/review 共用)
const ManageRoute = ({ children }) => {
  const isAuthenticated = sessionStorage.getItem('playerManageAuth') === 'true';
  return isAuthenticated ? children : <PasswordModal />;
};

const NavLinks = () => {
  const linkStyle = (active) => ({
    color: active ? '#fff' : 'rgba(255,255,255,0.75)',
    fontWeight: active ? 600 : 400,
    padding: '0 12px',
    borderBottom: active ? '2px solid #fff' : '2px solid transparent',
    height: '64px',
    display: 'inline-flex',
    alignItems: 'center',
    textDecoration: 'none',
    transition: 'all 0.2s'
  });
  return (
    <Space size={0} style={{ marginLeft: 32 }}>
      <NavLink to="/" style={({ isActive }) => linkStyle(isActive)}>首页</NavLink>
      <NavLink to="/changes" style={({ isActive }) => linkStyle(isActive)}>待调整球员</NavLink>
    </Space>
  );
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
            <Title level={3} style={{ color: '#fff', margin: 0 }}>
              ⚽ 实况足球球员筛选系统
            </Title>
            <NavLinks />
          </Header>

          <Content style={{ padding: '24px 50px' }}>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/changes" element={<ChangeRequestsPage />} />
              <Route path="/manage" element={<ManageRoute><PlayerManage /></ManageRoute>} />
              <Route path="/admin/review" element={<ManageRoute><AdminReviewPage /></ManageRoute>} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Content>
        </Layout>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;
