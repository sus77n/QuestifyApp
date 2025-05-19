import React from 'react';
import PrimaryInput from "../material/material";
import {ChevronRightIcon, UserIcon, LockClosedIcon, EnvelopeIcon} from '@heroicons/react/24/solid';
import {Link} from "react-router-dom";


const Signup = () => {
    return (
        <div
            className="relative flex flex-col h-screen w-full overflow-hidden"
        >
            <div className="fixed inset-0 -z-10">
                <img
                    src="/img/loginBackgroundMobile.svg"
                    alt="Mobile Background"
                    className="block md:hidden w-full h-full object-cover object-center"
                />

                <img
                    src="/img/loginBackground.svg"
                    alt="Desktop Background"
                    className="hidden md:block w-full h-full object-cover object-center"
                />
            </div>
            <main className="relative min-h-screen w-full justify-center items-center">
                <div className="relative z-10 top-[32%] md:top-[200px] left-[7%] md:left-[10%]">
                    <h1 className="text-3xl md:text-5xl text-text-color font-bold mb-10">Start your journey !</h1>
                    <form className="flex flex-col">
                        <div className="md:flex">
                            <div className="flex flex-col gap-6">
                                <EnvelopeIcon
                                    className="absolute left-4 md:left-5 md:top-[119px] top-[31%] transform -translate-y-1/2 h-5 w-5 md:h-8 md:w-8 text-text-color"/>
                                <PrimaryInput placeholder="Email" type="mail"/>
                                <UserIcon
                                    className="absolute left-4 md:left-5 md:top-[202px] top-[53%] transform -translate-y-1/2 h-5 w-5 md:h-8 md:w-8 text-text-color"/>
                                <PrimaryInput placeholder="Username" type="text"/>
                                <LockClosedIcon
                                    className="absolute left-4 md:left-5 md:top-[285px] top-[75%] transform -translate-y-1/2 h-5 w-5 md:h-8 md:w-8 text-text-color"/>
                                <PrimaryInput placeholder="Password" type="password"/>
                            </div>
                            <button className="md:ml-[50px] ml-[65%] flex-shrink-0 mt-[20px] ml-[50px]"
                                    style={{animation: 'bounceHorizontal 1s infinite'}}
                                    type="submit"
                            >
                                <ChevronRightIcon
                                    className="w-12 h-12 md:w-20 md:h-20 text-background-color bg-text-color rounded-full p-2"/>
                            </button>
                        </div>
                        <div className="mt-[-40px] md:mt-8 flex flex-col md:flex-row md:justify-between md:w-[490px] gap-4">
                            <p className="text-white font-semibold ">
                                Have an account ?
                                <Link
                                    to="/login"
                                    className="text-text-color sm:ml-1 relative group transition-all duration-300 hover:text-text-color/90"
                                >
                                      Sign in
                                    <span className="absolute bottom-0 left-0 w-0 h-px bg-text-color transition-all duration-300 group-hover:w-full"/>
                                </Link>
                            </p>
                        </div>
                    </form>
                </div>

            </main>
            <div className="container mx-auto p-4"></div>
            <style>{`
  @keyframes bounceHorizontal {
    0%, 100% { transform: translateX(0); }
    50% { transform: translateX(10px); }
  }
`}</style>
        </div>
    );
};


export default Signup;