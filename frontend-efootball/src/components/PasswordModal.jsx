import React, { useState } from 'react';
import { Modal, Input, Button, message } from 'antd';

const PasswordModal = ({ onSuccess, onCancel }) => {
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = () => {
    setLoading(true);

    // 验证密码
    if (password === 'wangyangexpo') {
      sessionStorage.setItem('playerManageAuth', 'true');
      message.success('验证成功');
      onSuccess();
    } else {
      message.error('密码错误，请重新输入');
    }

    setLoading(false);
  };

  return (
    <Modal
      open={true}
      title="管理页面验证"
      onCancel={onCancel}
      footer={null}
      centered
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