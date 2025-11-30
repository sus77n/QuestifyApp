import { useNavigate } from "react-router-dom";
import MyButton from "./material";
import {ArrowLeftIcon} from "@heroicons/react/24/solid";

export const Error403Page = () => {
    const navigate = useNavigate();

    return (
        <div className="flex items-center justify-center h-screen bg-gray-100 relative">
            {/* Background */}
            <img
                src="/img/403Page.png"
                className="block w-full h-full object-cover object-center z-0"
                alt="403 Page"
            />

            {/* Overlay Button */}
            <div className="absolute top-[73%] right-[63%] z-10">
                <MyButton
                    text="Back"
                    icon={<ArrowLeftIcon className="w-5 h-5" />}
                    onClick={() => navigate(-1)}
                />
            </div>
        </div>
    );
};

export const Error404Page = () => {
    const navigate = useNavigate();

    return (
        <div className="flex items-center justify-center h-screen bg-gray-100 relative">
            {/* Background */}
            <img
                src="/img/404Page.png"
                className="block w-full h-full object-cover object-center z-0"
                alt="404 Page"
            />

            {/* Overlay Button */}
            <div className="absolute top-[75%] right-[65%] z-10">
                <MyButton
                    text="Back"
                    icon={<ArrowLeftIcon className="w-5 h-5" />}
                    onClick={() => navigate(-1)}
                />
            </div>
        </div>
    );
};


export const Error500Page = () => {
    const navigate = useNavigate();

    return (
        <div className="flex items-center justify-center h-screen bg-gray-100 relative">

            {/* Background image */}
            <img
                src="/img/500Page.png"
                className="block w-full h-full object-cover object-center z-0"
                alt="500 Page"
            />

            {/* Overlay button */}
            <div className="absolute top-[70%] right-[65%] z-10">
                <MyButton
                    text="Back"
                    icon={<ArrowLeftIcon className="w-5 h-5" />}
                    onClick={() => navigate(-1)}
                />
            </div>

        </div>
    );
};

