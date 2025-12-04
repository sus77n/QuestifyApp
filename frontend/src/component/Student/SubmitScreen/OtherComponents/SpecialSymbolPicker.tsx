import React from "react";
import {specialSymbols} from "../../../material/sympolKeyboard";

export default function SpecialSymbolPicker({
                                                onInsert,
                                            }: {
    onInsert: (symbol: string) => void;
}) {
    return (
        <div className="my-6 p-3 border rounded-lg bg-gray-50">
            <h2 className="mb-2 font-semibold">Special Symbol Keyboard</h2>
            {specialSymbols.map((group) => (
                <div key={group.group} className="mb-2">
                    <p className="font-semibold text-gray-700">{group.group}</p>
                    <div className="flex flex-wrap gap-2 mt-1">
                        {group.symbols.map((s) => (
                            <button
                                key={s.value}
                                onClick={() => onInsert(s.value)}
                                title={s.tooltip}
                                className="px-2 py-1 border rounded-lg bg-white hover:bg-gray-100 text-lg"
                            >
                                {s.label}
                            </button>
                        ))}
                    </div>
                </div>
            ))}
        </div>
    );
}
