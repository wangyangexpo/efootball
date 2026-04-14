import React, { useState, useEffect, useCallback } from 'react';
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
  Tag,
  Row,
  Col,
  Upload,
  Image
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, ReloadOutlined, UploadOutlined, EyeOutlined, PictureOutlined } from '@ant-design/icons';
import { getPlayers, getPlayerEnums, createPlayer, updatePlayer, deletePlayer, extractData, uploadPlayerAvatar } from '../api/player';
import { trackEvent, EVENTS } from '../utils/analytics';

const { Option } = Select;

const PlayerManage = () => {
  const [players, setPlayers] = useState([]);
  const [enums, setEnums] = useState({});
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 20, total: 0 });
  const [modalOpen, setModalOpen] = useState(false);
  const [editingPlayer, setEditingPlayer] = useState(null);
  const [form] = Form.useForm();
  const [filterForm] = Form.useForm();
  const [filters, setFilters] = useState({});
  const [hasSearched, setHasSearched] = useState(false);

  // 加载枚举
  useEffect(() => {
    loadEnums();
  }, []);

  // 管理页面访问埋点（仅 mount 时触发一次）
  useEffect(() => {
    trackEvent(EVENTS.MANAGE_PAGE_VISIT);
  }, []);

  // 加载球员列表
  const loadPlayers = useCallback(async () => {
    if (!hasSearched) return;
    setLoading(true);
    try {
      const params = {
        ...filters,
        pageNum: pagination.current,
        pageSize: pagination.pageSize
      };
      // 移除 undefined 值
      Object.keys(params).forEach(key => {
        if (params[key] === undefined || params[key] === null || params[key] === '') {
          delete params[key];
        }
      });

      const response = await getPlayers(params);
      const data = extractData(response);
      setPlayers(data.list || []);
      setPagination(prev => ({ ...prev, total: data.total }));
    } catch (error) {
      message.error('加载球员列表失败');
    }
    setLoading(false);
  }, [filters, pagination.current, pagination.pageSize, hasSearched]);

  useEffect(() => {
    loadPlayers();
  }, [loadPlayers]);

  const loadEnums = async () => {
    try {
      const response = await getPlayerEnums();
      setEnums(extractData(response));
    } catch (error) {
      message.error('加载枚举失败');
    }
  };

  const handleTableChange = (newPagination) => {
    setPagination({
      ...pagination,
      current: newPagination.current,
      pageSize: newPagination.pageSize
    });
  };

  const handleFilterChange = () => {
    const values = filterForm.getFieldsValue();
    // 如果输入都为空，给出提示
    if (!values.id && !values.name) {
      message.warning('请输入球员ID或球员姓名进行查询');
      return;
    }
    setFilters(values);
    setHasSearched(true);
    setPagination(prev => ({ ...prev, current: 1 }));
  };

  const handleEnterPress = () => {
    handleFilterChange();
  };

  const handleResetFilter = () => {
    filterForm.resetFields();
    setFilters({});
    setHasSearched(false);
    setPlayers([]);
    setPagination(prev => ({ ...prev, current: 1, total: 0 }));
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
      {/* 筛选区域 */}
      <div style={{
        background: '#fff',
        padding: 16,
        marginBottom: 16,
        borderRadius: 8,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'flex-end'
      }}>
        <Form form={filterForm} layout="inline">
          <Form.Item name="id" style={{ marginBottom: 8 }}>
            <InputNumber placeholder="球员ID" min={1} style={{ width: 120 }} onPressEnter={handleEnterPress} />
          </Form.Item>
          <Form.Item name="name" style={{ marginBottom: 8 }}>
            <Input placeholder="球员姓名" allowClear style={{ width: 140 }} onPressEnter={handleEnterPress} />
          </Form.Item>
          <Form.Item style={{ marginBottom: 8 }}>
            <Space>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleFilterChange}>
                查询
              </Button>
              <Button icon={<ReloadOutlined />} onClick={handleResetFilter}>
                重置
              </Button>
            </Space>
          </Form.Item>
        </Form>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增
        </Button>
      </div>

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
          pagination={{
            ...pagination,
            position: ['bottomRight']
          }}
          onChange={handleTableChange}
          scroll={{ x: 900 }}
        />
      </div>

      <Modal
        open={modalOpen}
        title={editingPlayer ? '修改球员' : '新增球员'}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        width={800}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="name"
                label="球员姓名"
                rules={[{ required: true, message: '请输入球员姓名' }]}
              >
                <Input placeholder="请输入球员姓名" />
              </Form.Item>
            </Col>
            <Col span={12}>
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
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
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
            </Col>
            <Col span={12}>
              <Form.Item name="number" label="球衣号码" rules={[{ required: true, message: '请输入球衣号码' }]}>
                <InputNumber min={1} max={99} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="club" label="俱乐部" rules={[{ required: true, message: '请选择俱乐部' }]}>
                <Select placeholder="请选择俱乐部" showSearch allowClear>
                  {(enums.clubs || []).map(c => (
                    <Option key={c} value={c}>{c}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="league" label="联赛" rules={[{ required: true, message: '请选择联赛' }]}>
                <Select placeholder="请选择联赛" showSearch allowClear>
                  {(enums.leagues || []).map(l => (
                    <Option key={l} value={l}>{l}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="country" label="国家队" rules={[{ required: true, message: '请选择国家队' }]}>
                <Select placeholder="请选择国家队" showSearch allowClear>
                  {(enums.countries || []).map(c => (
                    <Option key={c} value={c}>{c}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="height" label="身高(cm)" rules={[{ required: true, message: '请输入身高' }]}>
                <InputNumber min={150} max={220} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="foot" label="惯用脚" rules={[{ required: true, message: '请选择惯用脚' }]}>
                <Select placeholder="请选择惯用脚" allowClear>
                  {(enums.foots || []).map(f => (
                    <Option key={f} value={f}>{f}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="cardImage" label="球员卡面">
                <Input placeholder="请输入图片URL，或点击右侧按钮上传" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={24}>
              <Form.Item label="球员卡面">
                <Form.Item noStyle shouldUpdate={(prev, cur) => prev.cardImage !== cur.cardImage}>
                  {() => {
                    const cardImageUrl = form.getFieldValue('cardImage');
                    const uploadAction = async ({ file, onSuccess, onError }) => {
                      if (!editingPlayer?.id) {
                        message.warning('新增球员请先点击确定保存后，再编辑卡面');
                        onError(new Error('请先保存'));
                        return;
                      }
                      try {
                        const response = await uploadPlayerAvatar(editingPlayer.id, file);
                        const result = response.data;
                        if (result.code !== '200') {
                          throw new Error(result.message || '上传失败');
                        }
                        const imageUrl = result.data;
                        form.setFieldValue('cardImage', imageUrl);
                        message.success('卡面上传成功，点击确定保存');
                        onSuccess();
                      } catch (err) {
                        message.error('卡面上传失败：' + (err.message || '请重试'));
                        onError(err);
                      }
                    };

                    if (cardImageUrl) {
                      // 已有卡面：显示图片 + 全屏预览 + 重新上传
                      return (
                        <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
                          <Image
                            src={cardImageUrl}
                            width={160}
                            height={220}
                            style={{ objectFit: 'cover', borderRadius: 8, border: '1px solid #eee' }}
                            preview={{ mask: <><EyeOutlined /> 预览</> }}
                          />
                          <Upload
                            accept="image/jpeg,image/png,image/webp,image/gif"
                            maxCount={1}
                            showUploadList={false}
                            customRequest={uploadAction}
                          >
                            <Button icon={<UploadOutlined />} size="small">重新上传</Button>
                          </Upload>
                        </div>
                      );
                    }

                    // 无卡面：显示大的点击上传区域
                    return (
                      <Upload
                        accept="image/jpeg,image/png,image/webp,image/gif"
                        maxCount={1}
                        showUploadList={false}
                        customRequest={uploadAction}
                      >
                        <div style={{
                          width: 160,
                          height: 220,
                          border: '1.5px dashed #d9d9d9',
                          borderRadius: 8,
                          display: 'flex',
                          flexDirection: 'column',
                          alignItems: 'center',
                          justifyContent: 'center',
                          cursor: editingPlayer?.id ? 'pointer' : 'not-allowed',
                          background: '#fafafa',
                          color: '#999',
                          gap: 8,
                          transition: 'border-color 0.2s',
                        }}
                          onMouseEnter={e => { if (editingPlayer?.id) e.currentTarget.style.borderColor = '#4096ff'; }}
                          onMouseLeave={e => { e.currentTarget.style.borderColor = '#d9d9d9'; }}
                        >
                          <PictureOutlined style={{ fontSize: 32, color: '#bbb' }} />
                          <span style={{ fontSize: 13 }}>
                            {editingPlayer?.id ? '点击上传卡面' : '保存后可上传卡面'}
                          </span>
                          <span style={{ fontSize: 11, color: '#ccc' }}>支持 JPG / PNG / WebP</span>
                        </div>
                      </Upload>
                    );
                  }}
                </Form.Item>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  );
};

export default PlayerManage;