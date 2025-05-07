import React from 'react';

const PrimaryInput = ({
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

export default PrimaryInput;