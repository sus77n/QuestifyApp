import React from 'react';

interface MyAlertProps {
    title: string;
    content: string;
    onClose: () => void;
}

export const MyAlert = ({ title, content, onClose }: MyAlertProps) => {
    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-lg max-w-md w-full">
                <h3 className="text-lg font-semibold mb-4">{title}</h3>
                <p className="mb-6">{content}</p>
                <div className="flex justify-end">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-text-color text-white rounded-lg hover:bg-white hove:text-text-color"
                    >
                        OK
                    </button>
                </div>
            </div>
        </div>
    );
};