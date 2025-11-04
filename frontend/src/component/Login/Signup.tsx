import React, { useState } from "react";
import { PrimaryInput, Spinner } from "../material/material";
import {
  ChevronRightIcon,
  UserIcon,
  LockClosedIcon,
  EnvelopeIcon,
} from "@heroicons/react/24/solid";
import { Link, useNavigate } from "react-router-dom";
import { useSignupMutation } from "../../API/service/auth.service";
import { toast } from "react-toastify";

const Signup = () => {
  const [formData, setFormData] = useState({
    email: "",
    username: "",
    password: "",
  });

  const [signup, { isLoading }] = useSignupMutation();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const result = await signup(formData);

      const response = result as typeof result & {
        meta?: { success: boolean; message: string; errorCode: string | null };
      };
      toast.success(response?.meta?.message || "Account created successfully!", {
        autoClose: 2000,
        onClose: () => navigate("/login"),
      });
    } catch (error: any) {
      const errorMessage = error?.data.message() || "Signup failed. Please try again.";
      toast.error(errorMessage, { autoClose: 3000 });
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
        <div className="relative z-10 top-[35%] md:top-[200px] left-[7%] md:left-[10%]">
          <h1 className="text-3xl md:text-5xl text-text-color font-bold mb-10">
            Start your journey!
          </h1>

          <form className="flex flex-col" onSubmit={handleSubmit}>
            <div className="md:flex">
              <div className="flex flex-col gap-6">
                <div className="relative">
                  <EnvelopeIcon className="absolute left-5 top-1/2 -translate-y-1/2 h-8 w-8 text-text-color" />
                  <PrimaryInput
                      placeholder="Email"
                      type="email"
                      name="email"
                      value={formData.email}
                      onChange={handleChange}
                      required
                  />
                </div>

                <div className="relative">
                  <UserIcon className="absolute left-5 top-1/2 -translate-y-1/2 h-8 w-8 text-text-color" />
                  <PrimaryInput
                      placeholder="Username"
                      type="text"
                      name="username"
                      value={formData.username}
                      onChange={handleChange}
                      required
                  />
                </div>

                <div className="relative">
                  <LockClosedIcon className="absolute left-5 top-1/2 -translate-y-1/2 h-8 w-8 text-text-color" />
                  <PrimaryInput
                      placeholder="Password"
                      type="password"
                      name="password"
                      value={formData.password}
                      onChange={handleChange}
                      required
                      minLength={8}
                      maxLength={40}
                  />
                </div>
              </div>

              {isLoading ? (
                <div className="md:ml-[50px] flex-shrink-0 mt-[100px] ml-[20px]">
                  <Spinner />
                </div>
              ) : (
                <button
                  className="md:ml-[50px] ml-[65%] flex-shrink-0 mt-[20px]"
                  style={{ animation: "bounceHorizontal 1s infinite" }}
                  type="submit"
                  disabled={isLoading}
                >
                  <ChevronRightIcon
                    className={`w-12 h-12 md:w-20 md:h-20 text-background-color bg-text-color rounded-full p-2 ${
                      isLoading ? "opacity-50" : ""
                    }`}
                  />
                </button>
              )}
            </div>

            <div className="mt-[-40px] md:mt-8 flex flex-col md:flex-row md:justify-between md:w-[490px] gap-4">
              <p className="text-white font-semibold">
                Have an account ?
                <Link
                  to="/login"
                  className="text-text-color sm:ml-1 relative group transition-all duration-300 hover:text-text-color/90"
                >
                  Sign in
                  <span className="absolute bottom-0 left-0 w-0 h-px bg-text-color transition-all duration-300 group-hover:w-full" />
                </Link>
              </p>
            </div>
          </form>
        </div>
      </main>

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
