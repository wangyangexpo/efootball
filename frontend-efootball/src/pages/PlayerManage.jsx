import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Space,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  message,
  Popconfirm,
  Tag
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { getPlayers, getPlayerEnums, createPlayer, updatePlayer, deletePlayer, extractData } from '../api/player';

const { Option } = Select;

const PlayerManage = () => {
  const [players, setPlayers] = useState([]);
  const [enums, setEnums] = useState({});
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 20, total: 0 });
  const [modalOpen, setModalOpen] = useState(false);
  const [editingPlayer, setEditingPlayer] = useState(null);
  const [form] = Form.useForm();

  // 加载枚举
  useEffect(() => {
    loadEnums();
  }, []);

  // 加载球员列表
  useEffect(() => {
    loadPlayers();
  }, [pagination.current, pagination.pageSize]);

  const loadEnums = async () => {
    try {
      const response = await getPlayerEnums();
      setEnums(extractData(response));
    } catch (error) {
      message.error('加载枚举失败');
    }
  };

  const loadPlayers = async () => {
    setLoading(true);
    try {
      const response = await getPlayers({
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      });
      const data = extractData(response);
      setPlayers(data.records || []);
      setPagination({ ...pagination, total: data.total });
    } catch (error) {
      message.error('加载球员列表失败');
    }
    setLoading(false);
  };

  const handleTableChange = (newPagination) => {
    setPagination({
      ...pagination,
      current: newPagination.current,
      pageSize: newPagination.pageSize
    });
  };

  const handleAdd = () => {
    setEditingPlayer(null);
    form.resetFields();
    setModalOpen(true);
  };

  const handleEdit = (record) => {
    setEditingPlayer(record);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleDelete = async (id) => {
    try {
      await deletePlayer(id);
      message.success('删除成功');
      loadPlayers();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      if (editingPlayer) {
        await updatePlayer(editingPlayer.id, values);
        message.success('修改成功');
      } else {
        await createPlayer(values);
        message.success('新增成功');
      }

      setModalOpen(false);
      loadPlayers();
    } catch (error) {
      if (error.response?.data?.message) {
        message.error(error.response.data.message);
      } else if (error.errorFields) {
        // Form validation error, ignore
      } else {
        message.error('操作失败');
      }
    }
  };

  const columns = [
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
      width: 120,
      render: (text) => <strong>{text}</strong>
    },
    {
      title: '位置',
      dataIndex: 'position',
      key: 'position',
      width: 80
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Tag color={status === '现役' ? 'green' : 'orange'}>{status}</Tag>
      )
    },
    {
      title: '号码',
      dataIndex: 'number',
      key: 'number',
      width: 60,
      align: 'center'
    },
    {
      title: '俱乐部',
      dataIndex: 'club',
      key: 'club',
      width: 120
    },
    {
      title: '联赛',
      dataIndex: 'league',
      key: 'league',
      width: 80
    },
    {
      title: '国家队',
      dataIndex: 'country',
      key: 'country',
      width: 80
    },
    {
      title: '身高',
      dataIndex: 'height',
      key: 'height',
      width: 60,
      align: 'center'
    },
    {
      title: '惯用脚',
      dataIndex: 'foot',
      key: 'foot',
      width: 60,
      align: 'center'
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            修改
          </Button>
          <Popconfirm
            title="确定删除该球员？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增球员
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={players}
        rowKey="id"
        loading={loading}
        pagination={pagination}
        onChange={handleTableChange}
        scroll={{ x: 900 }}
      />

      <Modal
        open={modalOpen}
        title={editingPlayer ? '修改球员' : '新增球员'}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="球员姓名"
            rules={[{ required: true, message: '请输入球员姓名' }]}
          >
            <Input placeholder="请输入球员姓名" />
          </Form.Item>

          <Form.Item
            name="position"
            label="位置"
            rules={[{ required: true, message: '请选择位置' }]}
          >
            <Select placeholder="请选择位置" showSearch>
              {(enums.positions || []).map(pos => (
                <Option key={pos} value={pos}>{pos}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="status"
            label="现役状态"
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select placeholder="请选择状态">
              {(enums.statuses || []).map(s => (
                <Option key={s} value={s}>{s}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="number" label="球衣号码">
            <InputNumber min={1} max={99} style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="club" label="俱乐部">
            <Select placeholder="请选择俱乐部" showSearch allowClear>
              {(enums.clubs || []).map(c => (
                <Option key={c} value={c}>{c}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="league" label="联赛">
            <Select placeholder="请选择联赛" showSearch allowClear>
              {(enums.leagues || []).map(l => (
                <Option key={l} value={l}>{l}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="country" label="国家队">
            <Select placeholder="请选择国家队" showSearch allowClear>
              {(enums.countries || []).map(c => (
                <Option key={c} value={c}>{c}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="height" label="身高(cm)">
            <InputNumber min={150} max={220} style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="foot" label="惯用脚">
            <Select placeholder="请选择惯用脚" allowClear>
              {(enums.foots || []).map(f => (
                <Option key={f} value={f}>{f}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="cardImage" label="卡面图片URL">
            <Input placeholder="请输入图片URL地址" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default PlayerManage;