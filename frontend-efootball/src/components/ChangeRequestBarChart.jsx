import React, { useMemo } from 'react';
import { Empty, Tooltip } from 'antd';
import './ChangeRequestBarChart.css';

const TOP_N = 15;
const Y_TICKS = 4;

/**
 * 提议热度柱状图(纯 CSS):
 * - X 轴:球员姓名
 * - Y 轴:点赞数(approveCount)
 * - 按 approveCount 倒序展示前 TOP_N 条
 */
const ChangeRequestBarChart = ({ items = [] }) => {
  const { bars, yMax, yTicks } = useMemo(() => {
    const sorted = [...items]
      .sort((a, b) => (b.approveCount || 0) - (a.approveCount || 0))
      .slice(0, TOP_N);
    const max = sorted.reduce((m, it) => Math.max(m, it.approveCount || 0), 0);
    // Y 轴最大值向上取整到合理刻度,至少为 1
    const niceMax = Math.max(1, Math.ceil(max / Y_TICKS) * Y_TICKS);
    const ticks = Array.from({ length: Y_TICKS + 1 }, (_, i) =>
      Math.round((niceMax / Y_TICKS) * (Y_TICKS - i))
    );
    return { bars: sorted, yMax: niceMax, yTicks: ticks };
  }, [items]);

  if (!bars.length) {
    return (
      <div className="cr-chart-card cr-chart-empty">
        <Empty description="暂无提议数据" image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </div>
    );
  }

  return (
    <div className="cr-chart-card">
      <div className="cr-chart-header">
        <span className="cr-chart-title">提议热度榜</span>
        <span className="cr-chart-sub">按点赞数倒序 · TOP {bars.length}</span>
      </div>

      <div className="cr-chart-body">
        <div className="cr-chart-yaxis">
          {yTicks.map(t => (
            <div key={t} className="cr-chart-ytick">{t}</div>
          ))}
        </div>

        <div className="cr-chart-plot">
          <div className="cr-chart-grid">
            {yTicks.map((_, i) => <div key={i} className="cr-chart-gridline" />)}
          </div>

          <div className="cr-chart-bars">
            {bars.map(item => {
              const count = item.approveCount || 0;
              const heightPct = yMax === 0 ? 0 : (count / yMax) * 100;
              return (
                <Tooltip
                  key={item.id}
                  title={`${item.currentName || '未知'} · ${count} 赞`}
                  placement="top"
                >
                  <div className="cr-chart-col">
                    <div className="cr-chart-count">{count}</div>
                    <div className="cr-chart-bar" style={{ height: `${heightPct}%` }} />
                    <div className="cr-chart-name" title={item.currentName}>
                      {item.currentName || '—'}
                    </div>
                  </div>
                </Tooltip>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChangeRequestBarChart;
