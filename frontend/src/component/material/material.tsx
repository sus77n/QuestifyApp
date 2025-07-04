import React from 'react';
import styled from "styled-components";

export const PrimaryInput = ({
                          placeholder = 'Enter text...',
                          w = 'w-[330px] md:w-[500px]', // Default width
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

export const MyButton: React.FC<{ onClick?: React.MouseEventHandler<HTMLButtonElement>; children: React.ReactNode; className?: string; disabled?: boolean }> = ({
                                                                                                                                                           onClick,
                                                                                                                                                           children,
                                                                                                                                                           className = '',
                                                                                                                                                           disabled = false,
                                                                                                                                                       }) => (
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
  border-top: 4px solid #02457A;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;

  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`;