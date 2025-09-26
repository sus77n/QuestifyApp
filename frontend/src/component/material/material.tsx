import React from "react";
import styled from "styled-components";

export const PrimaryInput = ({
  placeholder = "Enter text...",
  w = "w-[330px] md:w-[500px]", // Default width
  ...props
}) => {
  return (
    <input
      className={`
        bg-white
        h-[6vh] md:h-[60px]
        ${w} 
        rounded-full
        p-4
        pl-[42px] md:pl-[60px]
        text-lg
        border-2
        border-transparent
        focus:border-text-color
        focus:outline-none
        transition-all
        duration-200
        placeholder-gray-400
        placeholder-opacity-75
        hover:shadow-sm
        focus:shadow-md
        focus:ring-1
        focus:ring-primary
        focus:ring-opacity-50
        text-text-color
      `}
      placeholder={placeholder}
      {...props}
    />
  );
};

export const MyButton: React.FC<{
  onClick?: React.MouseEventHandler<HTMLButtonElement>;
  children: React.ReactNode;
  className?: string;
  disabled?: boolean;
}> = ({ onClick, children, className = "", disabled = false }) => (
  <button
    type="button" // fixed type here
    onClick={onClick}
    disabled={disabled}
    className={`text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 me-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800 ${className}`}
  >
    {children}
  </button>
);

export const Spinner = styled.div`
  border: 4px solid #eef1f9;
  border-radius: 50%;
  border-top: 4px solid #02457a;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
`;

interface MyButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  disabled?: boolean;
  isLoading?: boolean;
  color?: "primary" | "secondary" | "success" | "danger" | "warning" | "info";
  variant?: "contained" | "outlined" | "text";
  size?: "small" | "medium" | "large";
}

export const MyButtonAdvanced = ({
  children,
  onClick,
  disabled = false,
  isLoading = false,
  color = "primary",
  variant = "contained",
  size = "medium",
}: MyButtonProps) => {
  // Base classes that apply to all buttons
  const baseClasses =
    "rounded-lg font-medium transition-all flex items-center justify-center";

  // Size classes
  const sizeClasses = {
    small: "px-3 py-1 text-sm min-w-[90px]",
    medium: "px-4 py-2 text-base min-w-[120px]",
    large: "px-6 py-3 text-lg min-w-[150px]",
  };

  // Color classes for contained variant
  const containedColorClasses = {
    primary:
      "bg-text-color text-white hover:bg-white hover:text-text-color border-2 border-text-color",
    secondary: "bg-purple-500 text-white hover:bg-purple-600",
    success: "bg-green-500 text-white hover:bg-green-600",
    danger:
      "bg-red-500 text-white hover:bg-white hover:text-red-500 border-2 border-red-500",
    warning: "bg-yellow-500 text-white hover:bg-yellow-600",
    info: "bg-cyan-500 text-white hover:bg-cyan-600",
  };

  // Color classes for outlined variant
  const outlinedColorClasses = {
    primary: "border-2 border-blue-500 text-blue-500 hover:bg-blue-50",
    secondary: "border-2 border-purple-500 text-purple-500 hover:bg-purple-50",
    success: "border-2 border-green-500 text-green-500 hover:bg-green-50",
    danger: "border-2 border-red-500 text-red-500 hover:bg-red-50",
    warning: "border-2 border-yellow-500 text-yellow-500 hover:bg-yellow-50",
    info: "border-2 border-cyan-500 text-cyan-500 hover:bg-cyan-50",
  };

  // Color classes for text variant
  const textColorClasses = {
    primary: "text-blue-500 hover:bg-blue-50",
    secondary: "text-purple-500 hover:bg-purple-50",
    success: "text-green-500 hover:bg-green-50",
    danger: "text-red-500 hover:bg-red-50",
    warning: "text-yellow-500 hover:bg-yellow-50",
    info: "text-cyan-500 hover:bg-cyan-50",
  };

  // Disabled state classes
  const disabledClasses =
    variant === "contained"
      ? "bg-gray-300 text-gray-500 cursor-not-allowed"
      : variant === "outlined"
        ? "border-gray-300 text-gray-400 cursor-not-allowed"
        : "text-gray-400 cursor-not-allowed";

  // Determine the appropriate color classes based on variant
  const variantColorClasses =
    variant === "contained"
      ? containedColorClasses[color]
      : variant === "outlined"
        ? outlinedColorClasses[color]
        : textColorClasses[color];

  // Loading text based on variant
  const loadingText = variant === "contained" ? "Processing..." : "Loading...";

  return (
    <button
      onClick={onClick}
      disabled={disabled || isLoading}
      className={`
                ${baseClasses}
                ${sizeClasses[size]}
                ${disabled || isLoading ? disabledClasses : variantColorClasses}
                ${isLoading ? "opacity-75" : ""}
                ${variant === "text" ? "px-2" : ""} // Less padding for text variant
            `}
    >
      {isLoading ? (
        <>
          <svg
            className={`animate-spin -ml-1 mr-2 h-4 w-4 ${
              variant === "contained" ? "text-white" : `text-${color}-500`
            }`}
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            ></circle>
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            ></path>
          </svg>
          {loadingText}
        </>
      ) : (
        children
      )}
    </button>
  );
};
