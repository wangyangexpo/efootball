import React, { useState } from 'react';
import { LikeOutlined, LikeFilled, DislikeOutlined, DislikeFilled, CheckOutlined, CloseOutlined } from '@ant-design/icons';
import { Button, message, Modal, Input } from 'antd';
import './ChangeRequestCard.css';

const FIELD_DEFS = [
  { key: 'Name', label: '姓名' },
  { key: 'Position', label: '位置' },
  { key: 'Status', label: '现役' },
  { key: 'Number', label: '号码' },
  { key: 'Club', label: '俱乐部' },
  { key: 'League', label: '联赛' },
  { key: 'Country', label: '国家' },
  { key: 'Height', label: '身高' },
  { key: 'Foot', label: '惯用脚' }
];

const DEFAULT_IMG = '/default-card.svg';

/**
 * 修改提议卡片
 *
 * mode: 'public' | 'review'
 *   - public  普通访客视角:底部 👍 / 👎 按钮,onVote(voteType)
 *   - review  管理员视角:底部 采纳 / 驳回 按钮,onApprove() / onReject(reason)
 *
 * 关键交互:
 *   - 点击卡片本身翻转;footer 区域内的按钮 stopPropagation,不触发翻转
 */
const ChangeRequestCard = ({ data, mode = 'public', onVote, onApprove, onReject, busy }) => {
  const [flipped, setFlipped] = useState(false);
  const [rejectVisible, setRejectVisible] = useState(false);
  const [rejectReason, setRejectReason] = useState('');

  const toggleFlip = () => setFlipped(!flipped);

  const handleVote = (e, voteType) => {
    e.stopPropagation();
    if (!onVote) return;
    onVote(voteType);
  };

  const handleApprove = (e) => {
    e.stopPropagation();
    Modal.confirm({
      title: '确认采纳此修改?',
      content: '采纳后球员数据会被立即更新,且本卡片不再显示。',
      okText: '采纳',
      cancelText: '取消',
      onOk: () => onApprove && onApprove()
    });
  };

  const handleRejectClick = (e) => {
    e.stopPropagation();
    setRejectReason('');
    setRejectVisible(true);
  };

  const submitReject = () => {
    if (onReject) onReject(rejectReason);
    setRejectVisible(false);
  };

  const cardImg = DEFAULT_IMG;

  return (
    <div className="cr-card-wrap" onClick={toggleFlip}>
      <div className={`cr-card ${flipped ? 'cr-flipped' : ''}`}>
        {/* 正面 */}
        <div className="cr-face cr-front">
          <img
            className="cr-img"
            src={cardImg}
            alt={data.currentName}
            onError={(e) => { e.target.src = DEFAULT_IMG; }}
          />
          <div className="cr-name-badge">{data.currentName || '未知球员'}</div>
          <div className="cr-flip-hint">点击翻转查看修改详情</div>
        </div>

        {/* 反面 */}
        <div className="cr-face cr-back">
          <div className="cr-back-title">
            <span>{data.currentName} · 修改提议</span>
            <span className="cr-back-id">#{data.id}</span>
          </div>
          <div className="cr-diff-list">
            {FIELD_DEFS.map(({ key, label }) => {
              const oldVal = data[`current${key}`];
              const newVal = data[`proposed${key}`];
              const changed = newVal !== undefined && newVal !== null && newVal !== oldVal;
              return (
                <div key={key} className={`cr-diff-row ${changed ? 'cr-changed' : ''}`}>
                  <span className="cr-field">{label}</span>
                  <span className="cr-old">{display(oldVal)}</span>
                  <span className="cr-arrow">→</span>
                  <span className="cr-new">{display(newVal)}</span>
                </div>
              );
            })}
          </div>

          {/* footer */}
          <div className="cr-footer" onClick={(e) => e.stopPropagation()}>
            {mode === 'public' ? (
              <>
                <Button
                  size="small"
                  type={data.myVote === 1 ? 'primary' : 'default'}
                  icon={data.myVote === 1 ? <LikeFilled /> : <LikeOutlined />}
                  loading={busy}
                  onClick={(e) => handleVote(e, 1)}
                >
                  赞同 {data.approveCount || 0}
                </Button>
                <Button
                  size="small"
                  danger={data.myVote === -1}
                  icon={data.myVote === -1 ? <DislikeFilled /> : <DislikeOutlined />}
                  loading={busy}
                  onClick={(e) => handleVote(e, -1)}
                >
                  不赞同 {data.disapproveCount || 0}
                </Button>
              </>
            ) : (
              <>
                <Button
                  size="small"
                  type="primary"
                  icon={<CheckOutlined />}
                  loading={busy}
                  onClick={handleApprove}
                >
                  采纳
                </Button>
                <Button
                  size="small"
                  danger
                  icon={<CloseOutlined />}
                  loading={busy}
                  onClick={handleRejectClick}
                >
                  驳回
                </Button>
              </>
            )}
          </div>
        </div>
      </div>

      <Modal
        open={rejectVisible}
        title="驳回原因(选填)"
        onCancel={() => setRejectVisible(false)}
        onOk={submitReject}
        okText="确认驳回"
        cancelText="取消"
      >
        <Input.TextArea
          rows={3}
          value={rejectReason}
          onChange={(e) => setRejectReason(e.target.value)}
          placeholder="例如:与公开资料不符 / 重复提交"
        />
      </Modal>
    </div>
  );
};

const display = (v) => {
  if (v === null || v === undefined || v === '') return '—';
  return String(v);
};

export default ChangeRequestCard;
