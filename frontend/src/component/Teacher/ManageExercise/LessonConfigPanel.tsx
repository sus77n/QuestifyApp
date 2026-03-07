import React, { useEffect } from "react";
import { Form, Input, Switch, Button, message, Spin } from "antd";
import { CogIcon } from "@heroicons/react/24/solid";
import { CheckOutlined, SaveOutlined } from "@ant-design/icons";

import {
    useCreateLessonConfigMutation,
    useUpdateLessonConfigMutation,
} from "../../../API/service/lessonConfig.service";
import { LessonConfigDTO } from "../../../model/LessonConfigDTO";

interface Props {
    lessonId: string;
    data?: LessonConfigDTO | null;
}

const LessonConfigPanel: React.FC<Props> = ({ lessonId, data }) => {
    const [form] = Form.useForm();
    const [createConfig, { isLoading: isCreating }] = useCreateLessonConfigMutation();
    const [updateConfig, { isLoading: isUpdating }] = useUpdateLessonConfigMutation();
    const currentConfig = data;

    // 4. isBusy chỉ còn là trạng thái đang lưu
    const isBusy = isCreating || isUpdating;

    useEffect(() => {
        if (currentConfig) {
            form.setFieldsValue({
                questionsPerAttempt: currentConfig.questionsPerAttempt,
                passThreshold: currentConfig.passThreshold,
                noRepeatScope: currentConfig.noRepeatScope,
            });
        } else {
            form.resetFields();
        }
    }, [currentConfig, form]);

    const handleSave = async (values: any) => {
        try {
            const payload = {
                questionsPerAttempt: Number(values.questionsPerAttempt),
                passThreshold: Number(values.passThreshold),
                noRepeatScope: values.noRepeatScope || false,
            };

            if (currentConfig) {
                await updateConfig({
                    lessonId: currentConfig.lessonId,
                    data: { ...currentConfig, ...payload },
                }).unwrap();
                message.success("Lesson config updated!");
            } else {
                // Create
                await createConfig({
                    lessonId,
                    ...payload,
                } as LessonConfigDTO).unwrap();
                message.success("Lesson config created!");
            }
        } catch (error) {
            console.error(error);
            message.error("Failed to save config.");
        }
    };

    return (
        <div className="col-span-2 bg-white rounded-lg shadow-xl p-4 relative h-full">
            {isBusy && (
                <div className="absolute inset-0 z-10 bg-white/60 flex items-center justify-center rounded-lg">
                    <Spin />
                </div>
            )}

            <div className="flex justify-between items-center mb-3">
                <div className="flex gap-2">
                    <CogIcon width={20} className="text-text-color" />
                    <p className="text-text-color font-semibold">Lesson Config</p>
                </div>
                <Button
                    type="primary"
                    size="small"
                    icon={<SaveOutlined />}
                    onClick={() => form.submit()}
                    loading={isBusy}
                >
                    Save
                </Button>
            </div>

            <div className="flex flex-col">
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSave}
                    initialValues={{ noRepeatScope: false }}
                >
                    <Form.Item
                        label="Questions per attempt"
                        name="questionsPerAttempt"
                        className="mb-3"
                        rules={[{ required: true, message: 'Required' }]}
                    >
                        <Input placeholder="Ex: 10" type="number" />
                    </Form.Item>

                    <Form.Item
                        label="Pass Threshold (%)"
                        name="passThreshold"
                        className="mb-3"
                        rules={[{ required: true, message: 'Required' }]}
                    >
                        <Input
                            placeholder="Ex: 80"
                            type="number"
                            min={0}
                            max={100}
                            suffix="%"
                        />
                    </Form.Item>

                    <div className="flex flex-row items-center justify-between mb-1 pt-2 border-t border-gray-100">
                        <span className="text-gray-700 font-normal text-sm">
                            No repeat exercise:
                        </span>

                        <Form.Item
                            name="noRepeatScope"
                            valuePropName="checked"
                            noStyle
                        >
                            <Switch
                                size="small"
                                checkedChildren={<CheckOutlined />}
                                unCheckedChildren="No"
                            />
                        </Form.Item>
                    </div>
                </Form>
            </div>
        </div>
    );
};

export default LessonConfigPanel;