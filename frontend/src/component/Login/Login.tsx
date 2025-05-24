import { useState } from 'react';
import { useLoginMutation } from '../../API/service/auth.service';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import {PrimaryInput} from "../material/material";
import {ChevronRightIcon, UserIcon, LockClosedIcon} from '@heroicons/react/24/solid';
import { Link } from 'react-router-dom';

const Login = () => {
    const [login, { isLoading }] = useLoginMutation();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const response = await login(formData).unwrap();
            toast.success('Login successful!');
            // You might want to store the token/user data here
            // localStorage.setItem('token', response.token);
            navigate('/'); // Redirect to home or dashboard
        } catch (error) {
            toast.error('Login failed. Please check your credentials.');
            console.error('Login error:', error);
        }
    };

    return (
        <div className="relative flex flex-col h-screen w-full overflow-hidden">
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
                <div className="relative z-10 top-[23%] md:top-[200px] left-[7%] md:left-[10%]">
                    <h1 className="text-3xl md:text-5xl text-text-color font-bold mb-10">Welcome !</h1>
                    <form className="flex flex-col" onSubmit={handleSubmit}>
                        <div className="flex">
                            <div className="flex flex-col gap-6">
                                <UserIcon
                                    className="absolute left-4 md:left-5 md:top-[119px] top-[34%] transform -translate-y-1/2 h-5 w-5 md:h-8 md:w-8 text-text-color"/>
                                <PrimaryInput
                                    placeholder="Email"
                                    w="w-[270px] md:w-[500px]"
                                    type="text"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleInputChange}
                                />
                                <LockClosedIcon
                                    className="absolute left-4 md:left-5 md:top-[202px] top-[58%] transform -translate-y-1/2 h-5 w-5 md:h-8 md:w-8 text-text-color"/>
                                <PrimaryInput
                                    placeholder="Password"
                                    w="w-[270px] md:w-[500px]"
                                    type="password"
                                    name="password"
                                    value={formData.password}
                                    onChange={handleInputChange}
                                />
                            </div>
                            <button
                                className="md:ml-[50px] ml-[10px] flex-shrink-0 mt-[20px] ml-[50px]"
                                style={{animation: 'bounceHorizontal 1s infinite'}}
                                type="submit"
                                disabled={isLoading}
                            >
                                <ChevronRightIcon
                                    className="w-12 h-12 md:w-20 md:h-20 text-background-color bg-text-color rounded-full p-2"/>
                            </button>
                        </div>
                        <div className="mt-[34px] md:mt-8 flex flex-col md:flex-row md:justify-between md:w-[490px] gap-4">
                            <a
                                className="text-white font-semibold relative pb-1 group transition-all duration-300 hover:text-opacity-90"
                            >
                                Forgot password ?
                                <span className="absolute bottom-0 left-0 w-0 h-px bg-white transition-all duration-300 hidden md:block group-hover:w-full"></span>
                            </a>
                            <p className="text-white font-semibold ">
                                Don't have an account ?
                                <Link
                                    to="/signup"
                                    className="text-text-color sm:ml-1 relative group transition-all duration-300 hover:text-text-color/90"
                                >
                                    Sign up
                                    <span
                                        className="absolute bottom-0 left-0 w-0 h-px bg-text-color transition-all duration-300 group-hover:w-full"/>
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

export default Login;