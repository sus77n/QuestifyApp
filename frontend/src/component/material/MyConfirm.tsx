import React from "react";

interface MyConfirmProps {
  title: string;
  content: string;
  onCancel: () => void;
  onConfirm: () => void;
  cancelText?: string;
  confirmText?: string;
}

export const MyConfirm = ({
  title,
  content,
  onCancel,
  onConfirm,
  cancelText = "Cancel",
  confirmText = "Confirm",
}: MyConfirmProps) => {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg max-w-md w-full overflow-x-auto">
        <h3 className="text-lg font-semibold mb-4 bg-text-color text-white p-3">
          {title}
        </h3>
        <p className="mb-6 p-3">{content}</p>
        <div className="flex justify-end gap-3 p-3">
          <button
            onClick={onCancel}
            className="px-4 py-2 border  text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
          >
            {cancelText}
          </button>
          <button
            onClick={onConfirm}
            className="px-4 py-2 bg-text-color text-white rounded-lg hover:bg-white hover:text-text-color hover:border-[1px] border-text-color"
          >
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
};
