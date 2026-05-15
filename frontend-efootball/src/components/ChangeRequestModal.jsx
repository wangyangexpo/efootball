import React, { useState, useEffect } from 'react';
import { Modal, Form, Select, InputNumber, Button, message, Space } from 'antd';
import { submitChangeRequest, extractData, extractError } from '../api/changeRequest';
import { getVisitorId } from '../utils/visitorId';

const { Option } = Select;

/**
 * 提交修改提议弹窗
 * 9 个字段预填当前球员数据;提交时只传与原值不同的字段;空 diff 禁用按钮。
 */
const ChangeRequestModal = ({ open, player, onClose, onSubmitted, enums }) => {
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);
  const [hasDiff, setHasDiff] = useState(false);

  useEffect(() => {
    if (open && player) {
      form.setFieldsValue({
        proposedName: player.name,
        proposedPosition: player.position,
        proposedStatus: player.status,
        proposedNumber: player.number,
        proposedClub: player.club,
        proposedLeague: player.league,
        proposedCountry: player.country,
        proposedHeight: player.height,
        proposedFoot: player.foot
      });
      setHasDiff(false);
    }
  }, [open, player, form]);

  const computeDiff = () => {
    if (!player) return false;
    const v = form.getFieldsValue();
    return (
      v.proposedName !== player.name ||
      v.proposedPosition !== player.position ||
      v.proposedStatus !== player.status ||
      v.proposedNumber !== player.number ||
      v.proposedClub !== player.club ||
      v.proposedLeague !== player.league ||
      v.proposedCountry !== player.country ||
      v.proposedHeight !== player.height ||
      v.proposedFoot !== player.foot
    );
  };

  const handleValuesChange = () => setHasDiff(computeDiff());

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      const payload = {
        playerId: player.id,
        submitterId: getVisitorId(),
        ...values
      };
      const resp = await submitChangeRequest(payload);
      if (resp.data?.code === '200') {
        message.success('提议已提交,等待审批');
        onSubmitted && onSubmitted(extractData(resp));
        onClose();
      } else {
        message.error(extractError(resp) || '提交失败');
      }
    } catch (e) {
      if (e?.errorFields) return; // 表单校验失败,Antd 自带提示
      message.error('提交失败:' + (e?.response?.data?.message || e.message));
    } finally {
      setSubmitting(false);
    }
  };

  if (!player) return null;

  return (
    <Modal
      open={open}
      title={`提议修改 - ${player.name}`}
      onCancel={onClose}
      footer={[
        <Button key="cancel" onClick={onClose}>取消</Button>,
        <Button
          key="submit"
          type="primary"
          disabled={!hasDiff}
          loading={submitting}
          onClick={handleSubmit}
        >
          {hasDiff ? '提交修改' : '请至少修改一项'}
        </Button>
      ]}
      width={600}
      destroyOnClose
    >
      <div style={{ color: '#888', fontSize: 12, marginBottom: 12 }}>
        修改后会进入待审批列表,经管理员采纳后球员数据才会更新。
      </div>
      <Form
        form={form}
        layout="vertical"
        onValuesChange={handleValuesChange}
      >
        <Space.Compact block>
          <Form.Item label="姓名" style={{ flex: 1, marginRight: 8 }}>
            <div style={{
              padding: '4px 11px',
              minHeight: 32,
              lineHeight: '24px',
              color: 'rgba(0, 0, 0, 0.88)'
            }}>
              {player.name}
            </div>
          </Form.Item>
          <Form.Item label="位置" name="proposedPosition" style={{ flex: 1 }}>
            <Select allowClear>
              {(enums?.positions || []).map(p => <Option key={p} value={p}>{p}</Option>)}
            </Select>
          </Form.Item>
        </Space.Compact>

        <Space.Compact block>
          <Form.Item label="现役状态" name="proposedStatus" style={{ flex: 1, marginRight: 8 }}>
            <Select allowClear>
              <Option value="现役">现役</Option>
              <Option value="历史">历史</Option>
            </Select>
          </Form.Item>
          <Form.Item label="球衣号码" name="proposedNumber" style={{ flex: 1 }}>
            <InputNumber style={{ width: '100%' }} min={1} max={99} />
          </Form.Item>
        </Space.Compact>

        <Space.Compact block>
          <Form.Item label="俱乐部" name="proposedClub" style={{ flex: 1, marginRight: 8 }}>
            <Select allowClear showSearch optionFilterProp="children">
              {(enums?.clubs || []).map(c => <Option key={c} value={c}>{c}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item label="联赛" name="proposedLeague" style={{ flex: 1 }}>
            <Select allowClear showSearch>
              {(enums?.leagues || []).map(l => <Option key={l} value={l}>{l}</Option>)}
            </Select>
          </Form.Item>
        </Space.Compact>

        <Space.Compact block>
          <Form.Item label="国家队" name="proposedCountry" style={{ flex: 1, marginRight: 8 }}>
            <Select allowClear showSearch optionFilterProp="children">
              {(enums?.countries || []).map(c => <Option key={c} value={c}>{c}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item label="身高(cm)" name="proposedHeight" style={{ flex: 1, marginRight: 8 }}>
            <InputNumber style={{ width: '100%' }} min={140} max={220} />
          </Form.Item>
          <Form.Item label="惯用脚" name="proposedFoot" style={{ flex: 1 }}>
            <Select allowClear>
              <Option value="左">左</Option>
              <Option value="右">右</Option>
            </Select>
          </Form.Item>
        </Space.Compact>
      </Form>
    </Modal>
  );
};

export default ChangeRequestModal;
