import React from "react";
import { Alert, Button, Input } from "antd";

export interface MatchingOption {
    id?: string;
    header: string;
    text: string;
    // Thêm metadata để khớp với JSON từ API
    metadata?: {
        side: "left" | "right";
    };
    // Giữ lại side ở ngoài để dự phòng cho các item được tạo mới chưa gọi API
    side?: "left" | "right"; 
}

export interface MatchingCorrectPair {
    leftHeader: string;
    rightHeader: string;
}

export interface MatchingBuilderValue {
    options: MatchingOption[];
    correctAnswers: MatchingCorrectPair[];
}

export interface MatchingBuilderProps {
    value: MatchingBuilderValue;
    onChange: (value: MatchingBuilderValue) => void;
    error?: string | null;
}

const MatchingBuilder: React.FC<MatchingBuilderProps> = ({
                                                             value,
                                                             onChange, error
                                                         }) => {
    // Đảm bảo options luôn là mảng để tránh lỗi crash nếu API trả về null/undefined
    const options = value?.options || [];
    const correctAnswers = value?.correctAnswers || [];

    /* =========================
       Helpers
    ========================== */

    // Hàm lấy side an toàn từ cả API (metadata.side) hoặc State tạo mới (side)
    const getSide = (o: MatchingOption) => o.metadata?.side || o.side;

    // luôn đảm bảo mỗi header có 1 left + 1 right
    const normalizePairs = (opts: MatchingOption[]): MatchingOption[] => {
        const grouped: Record<string, { left?: MatchingOption; right?: MatchingOption }> = {};

        for (const o of opts) {
            if (!grouped[o.header]) grouped[o.header] = {};
            const side = getSide(o);
            if (side === "left" || side === "right") {
                grouped[o.header][side] = o;
            }
        }

        const headers = Object.keys(grouped).sort((a, b) => Number(a) - Number(b));
        const result: MatchingOption[] = [];

        headers.forEach((h) => {
            const g = grouped[h];
            const left: MatchingOption =
                g.left ?? { header: h, metadata: { side: "left" }, text: "" };
            const right: MatchingOption =
                g.right ?? { header: h, metadata: { side: "right" }, text: "" };
            result.push(left, right);
        });

        return result;
    };

    const normalizedOptions = normalizePairs(options);
    const leftOptions = normalizedOptions.filter((o) => getSide(o) === "left");
    const rightOptions = normalizedOptions.filter((o) => getSide(o) === "right");

    /* =========================
       Update text
    ========================== */
    const updateText = (
        header: string,
        side: "left" | "right",
        text: string
    ) => {
        const updated = normalizedOptions.map((o) =>
            o.header === header && getSide(o) === side ? { ...o, text } : o
        );

        // correctAnswers: mặc định leftHeader = rightHeader
        const headers = Array.from(
            new Set(updated.map((o) => o.header))
        ).sort((a, b) => Number(a) - Number(b));

        const newCorrect: MatchingCorrectPair[] = headers.map((h) => ({
            leftHeader: h,
            rightHeader: h
        }));

        onChange({
            options: updated,
            correctAnswers: newCorrect
        });
    };

    /* =========================
       Add pair
    ========================== */
    const addPair = () => {
        const headers = normalizedOptions.map((o) => Number(o.header) || 0);
        const maxHeader = headers.length ? Math.max(...headers) : 0;
        const header = String(maxHeader + 1);

        // Khi tạo mới, ta bọc side vào trong metadata luôn để nhất quán với dữ liệu API
        const left: MatchingOption = { header, metadata: { side: "left" }, text: "" };
        const right: MatchingOption = { header, metadata: { side: "right" }, text: "" };

        const updated = [...normalizedOptions, left, right];
        const newCorrect: MatchingCorrectPair[] = [
            ...correctAnswers.filter((c) => c.leftHeader !== header),
            { leftHeader: header, rightHeader: header }
        ];

        onChange({
            options: updated,
            correctAnswers: newCorrect
        });
    };

    /* =========================
       Remove pair
    ========================== */
    const removePair = (header: string) => {
        const filtered = normalizedOptions.filter((o) => o.header !== header);

        // reheader 1..n theo thứ tự hiện tại
        const pairHeaders = Array.from(
            new Set(filtered.map((o) => o.header))
        ).sort((a, b) => Number(a) - Number(b));

        let counter = 1;
        const headerMap: Record<string, string> = {};
        pairHeaders.forEach((h) => {
            headerMap[h] = String(counter++);
        });

        const reheadered = filtered.map((o) => ({
            ...o,
            header: headerMap[o.header]
        }));

        const newCorrect: MatchingCorrectPair[] = reheadered.length
            ? Array.from(new Set(reheadered.map((o) => o.header))).map((h) => ({
                leftHeader: h,
                rightHeader: h
            }))
            : [];

        onChange({
            options: reheadered,
            correctAnswers: newCorrect
        });
    };

    /* =========================
       Render
    ========================== */
    return (
        <div className="space-y-2">

            <Alert
                message="Instructions"
                description={
                    <ul className="list-disc list-inside mt-1 text-sm text-gray-600 space-y-1">
                        <li>In the <strong>Question</strong> box above, provide clear instructions <em>(e.g., "Match each term with its correct definition")</em>.</li>
                        <li>Enter the correct matching pairs side-by-side below.</li>
                        <li><span className="text-orange-600 font-medium">Note:</span> The system will automatically shuffle the options for the students.</li>
                        <li>Click the <strong>"+ Add Pair"</strong> button to add more matching items.</li>
                    </ul>
                }
                type="info"
                showIcon
                closable
            />

            {/* Error display */}
            {error && <Alert message={error} type="error" showIcon className="mb-2" />}
            
            {leftOptions.map((left) => {
                const right = rightOptions.find((r) => r.header === left.header);

                return (
                    <div
                        key={left.header}
                        className="border rounded-lg p-4 space-y-3"
                    >
                        <div className="flex gap-3 items-center">
                            <span className="font-semibold text-gray-600 whitespace-nowrap">
                                Pair #{left.header}
                            </span>

                            <Input
                                className="flex-1"
                                placeholder="Left text"
                                value={left.text}
                                onChange={(e) =>
                                    updateText(left.header, "left", e.target.value)
                                }
                            />

                            <Input
                                className="flex-1"
                                placeholder="Right text"
                                value={right?.text ?? ""}
                                onChange={(e) =>
                                    updateText(left.header, "right", e.target.value)
                                }
                            />

                            <Button
                                danger
                                size="small"
                                onClick={() => removePair(left.header)}
                            >
                                X
                            </Button>
                        </div>
                    </div>
                );
            })}

            <Button type="dashed" onClick={addPair}>
                + Add Pair
            </Button>
        </div>
    );
};

export default MatchingBuilder;