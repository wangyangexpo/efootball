import React from 'react';
import { Table, Tag, Image } from 'antd';

const PlayerTable = ({ players, loading, pagination, onPageChange }) => {
  const columns = [
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 150,
      fixed: 'left',
      render: (text) => <strong>{text}</strong>
    },
    {
      title: '位置',
      dataIndex: 'position',
      key: 'position',
      width: 100,
      render: (position) => {
        const colorMap = {
          '门将': '#f59e0b',
          '中后卫': '#667eea',
          '左后卫': '#667eea',
          '右后卫': '#667eea',
          '后腰': '#06b6d4',
          '中前卫': '#10b981',
          '左前卫': '#10b981',
          '右前卫': '#10b981',
          '前腰': '#8b5cf6',
          '左边锋': '#ec4899',
          '右边锋': '#ec4899',
          '影锋': '#f97316',
          '中锋': '#ef4444'
        };
        return <Tag color={colorMap[position] || 'default'}>{position}</Tag>;
      }
    },
    {
      title: '现役状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status) => (
        <Tag color={status === '现役' ? '#667eea' : '#f59e0b'}>
          {status}
        </Tag>
      )
    },
    {
      title: '球衣号码',
      dataIndex: 'number',
      key: 'number',
      width: 100,
      align: 'center'
    },
    {
      title: '俱乐部',
      dataIndex: 'club',
      key: 'club',
      width: 150
    },
    {
      title: '联赛',
      dataIndex: 'league',
      key: 'league',
      width: 120
    },
    {
      title: '国家队',
      dataIndex: 'country',
      key: 'country',
      width: 120
    },
    {
      title: '身高(cm)',
      dataIndex: 'height',
      key: 'height',
      width: 100,
      align: 'center',
      sorter: true
    },
    {
      title: '惯用脚',
      dataIndex: 'foot',
      key: 'foot',
      width: 100,
      align: 'center',
      render: (foot) => (
        <Tag color={foot === '左' ? '#667eea' : '#10b981'}>{foot}</Tag>
      )
    },
    {
      title: '卡面',
      dataIndex: 'cardImage',
      key: 'cardImage',
      width: 100,
      align: 'center',
      render: (url) => url ? (
        <Image src={url} width={60} height={80} style={{ objectFit: 'cover' }} />
      ) : null
    }
  ];

  const tablePagination = pagination ? {
    current: pagination.current || 1,
    pageSize: pagination.pageSize || 20,
    total: pagination.total || 0,
    showSizeChanger: true,
    showTotal: (total) => `共 ${total} 名球员`,
    position: ['bottomCenter']
  } : false;

  return (
    <div style={{
      background: '#fff',
      padding: '24px',
      borderRadius: '12px',
      boxShadow: '0 4px 12px rgba(102, 126, 234, 0.08), 0 2px 6px rgba(102, 126, 234, 0.04)',
      border: '1px solid rgba(102, 126, 234, 0.1)'
    }}>
      <Table
        columns={columns}
        dataSource={players}
        rowKey="id"
        loading={loading}
        pagination={tablePagination}
        onChange={(newPagination) => onPageChange && onPageChange(newPagination)}
        scroll={{ x: 1200 }}
      />
    </div>
  );
};

export default PlayerTable;