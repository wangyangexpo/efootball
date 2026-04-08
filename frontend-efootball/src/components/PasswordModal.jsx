import React, { useState } from 'react';
import { Modal, Input, Button, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import md5 from 'md5';

// 密码的 MD5 值
const PASSWORD_MD5 = '6b09e658e9143361008d26983cc738ec';

const PasswordModal = () => {
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = () => {
    setLoading(true);

    // 验证密码 - 使用 MD5 对比
    if (md5(password) === PASSWORD_MD5) {
      sessionStorage.setItem('playerManageAuth', 'true');
      message.success('验证成功');
      window.location.reload();
    } else {
      message.error('密码错误，请重新输入');
      navigate('/');
    }

    setLoading(false);
  };

  return (
    <Modal
      open={true}
      title="管理页面验证"
      onCancel={() => navigate('/')}
      footer={null}
      centered
      width={360}
    >
      <div style={{ padding: '20px 0' }}>
        <Input.Password
          placeholder="请输入管理密码"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          onPressEnter={handleSubmit}
          style={{ marginBottom: 16 }}
        />
        <Button
          type="primary"
          block
          onClick={handleSubmit}
          loading={loading}
        >
          验证
        </Button>
      </div>
    </Modal>
  );
};

export default PasswordModal;