import React from "react";
import { Form, Select, InputNumber, Radio, Row, Col, Button } from "antd";

const { Option } = Select;

const FilterPanel = ({ filters, onFilterChange, enums }) => {
  const [form] = Form.useForm();

  const handleValuesChange = (changedValues, allValues) => {
    onFilterChange(allValues);
  };

  const handleReset = () => {
    form.resetFields();
    onFilterChange({
      position: undefined,
      status: undefined,
      number: undefined,
      league: undefined,
      club: undefined,
      country: undefined,
      foot: undefined,
      height: undefined,
      heightOperator: "=",
    });
  };

  return (
    <div
      style={{
        background: "#fff",
        padding: "24px",
        marginBottom: "24px",
        borderRadius: "12px",
        boxShadow:
          "0 4px 12px rgba(102, 126, 234, 0.08), 0 2px 6px rgba(102, 126, 234, 0.04)",
        border: "1px solid rgba(102, 126, 234, 0.1)",
      }}
    >
      <Form
        form={form}
        layout="vertical"
        onValuesChange={handleValuesChange}
        initialValues={{
          heightOperator: "=",
        }}
      >
        <Row gutter={16}>
          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="球员位置" name="position">
              <Select
                placeholder="请选择位置"
                allowClear
                showSearch
                optionFilterProp="children"
              >
                {enums.positions.map((pos) => (
                  <Option key={pos} value={pos}>
                    {pos}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="是否现役" name="status">
              <Select placeholder="请选择状态" allowClear>
                <Option value="现役">现役</Option>
                <Option value="历史">历史</Option>
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="球衣号码" name="number">
              <InputNumber
                placeholder="请输入号码"
                min={1}
                max={99}
                precision={0}
                style={{ width: "100%" }}
              />
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="所在联赛" name="league">
              <Select
                placeholder="请选择联赛"
                allowClear
                showSearch
                optionFilterProp="children"
              >
                {enums.leagues
                  .filter((league) => league)
                  .map((league) => (
                    <Option key={league} value={league}>
                      {league}
                    </Option>
                  ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="俱乐部" name="club">
              <Select
                placeholder="请选择俱乐部"
                allowClear
                showSearch
                optionFilterProp="children"
              >
                {enums.clubs.map((club) => (
                  <Option key={club} value={club}>
                    {club}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="国家队" name="country">
              <Select
                placeholder="请选择国家队"
                allowClear
                showSearch
                optionFilterProp="children"
              >
                {enums.countries.map((country) => (
                  <Option key={country} value={country}>
                    {country}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="精确身高(cm)" name="height">
              <InputNumber
                placeholder="请输入身高"
                min={150}
                max={220}
                style={{ width: "100%" }}
              />
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="身高条件">
              <Form.Item name="heightOperator" noStyle>
                <Radio.Group>
                  <Radio value="=">等于</Radio>
                  <Radio value="+">超过</Radio>
                  <Radio value="-">低于</Radio>
                </Radio.Group>
              </Form.Item>
            </Form.Item>
          </Col>

          <Col xs={24} sm={12} md={8} lg={6}>
            <Form.Item label="惯用脚" name="foot">
              <Select placeholder="请选择惯用脚" allowClear>
                <Option value="左">左</Option>
                <Option value="右">右</Option>
              </Select>
            </Form.Item>
          </Col>

          <Col xs={24} sm={24} md={24} lg={6}>
            <Form.Item label=" ">
              <Button type="primary" danger onClick={handleReset} block>
                重置筛选
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </div>
  );
};

export default FilterPanel;
