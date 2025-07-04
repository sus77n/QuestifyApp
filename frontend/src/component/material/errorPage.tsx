import {useNavigation} from "../../context/NavigationContext";

export const Error403Page = () => {
    const { setActiveTab } = useNavigation();

    return (
        <div className="flex items-center justify-center h-screen bg-gray-100 relative">
            <img src="/img/403Page.png" className="block w-full h-full object-cover object-center"
                 alt="403 Page"/>
            <button className="bg-text-color text-white rounded-xl px-5 py-2 text-sm font-bold
                                       border-2 border-text-color transition-colors duration-300
                                        hover:bg-white hover:text-text-color mt-96 ml-[-505px] absolute"
                    onClick={() => setActiveTab('My profile')}>Back to Profile
            </button>
        </div>
    );
};

export const Error404Page = () => {
    const { setActiveTab } = useNavigation();

    return (
        <div className="flex items-center justify-center h-screen bg-gray-100 relative">
            <img src="/img/404Page.png" className="block w-full h-full object-cover object-center"
                 alt="404 Page"/>
            <button className="bg-text-color text-white rounded-xl px-5 py-2 text-sm font-bold
                                       border-2 border-text-color transition-colors duration-300
                                        hover:bg-white hover:text-text-color mt-[460px] ml-[-505px] absolute"
                    onClick={() => setActiveTab('My profile')}>Back to Profile
            </button>
        </div>
    );
}

export const Error500Page = () => {
    const { setActiveTab } = useNavigation();

    return (
        <div className="flex items-center justify-center h-screen bg-gray-100 relative">
            <img src="/img/500Page.png" className="block w-full h-full object-cover object-center"
                 alt="500 Page"/>
            <button className="bg-text-color text-white rounded-xl px-5 py-2 text-sm font-bold
                                       border-2 border-text-color transition-colors duration-300
                                        hover:bg-white hover:text-text-color mt-96 ml-[-560px] absolute"
                    onClick={() => setActiveTab('My profile')}>Back to Profile
            </button>
        </div>
    );
}

