import { useState } from "react";
import { useLoginMutation } from "../../API/service/auth.service";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { PrimaryInput, Spinner } from "../material/material";
import {
  ChevronRightIcon,
  UserIcon,
  LockClosedIcon,
} from "@heroicons/react/24/solid";
import { Link } from "react-router-dom";
import {setAuth} from "../../utils/AuthUtils";

const Login = () => {
  const [login, { isLoading }] = useLoginMutation();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    usernameOrEmail: "",
    password: "",
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const result = await login(formData);

      const typedResult = result as typeof result & {
        meta?: { success: boolean; message: string; errorCode: string | null };
      };

      const user = typedResult.data;
      const message = typedResult.meta?.message || "Login successful!";

      if (!user) {
        throw new Error("No user data returned from server");
      }

      // Lưu user/token vào context hoặc localStorage
      setAuth(user);
      localStorage.setItem("token", user.token);

      switch (user.role) {
        case "ADMIN":
          navigate("/admin/dashboard");
          break;
        case "TEACHER":
        case "STUDENT":
        default:
          navigate("/courses");
          break;
      }

      toast.success(message);
    } catch (error: any) {

      if (error?.status === 500) {
        navigate("/500");
      }

      const backendMessage =
          error?.data?.message || "Login failed. Please check your credentials.";

      toast.error(backendMessage);
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
        <div className="relative z-10 top-[30%] md:top-[250px] left-[7%] md:left-[10%]">
          <h1 className="text-3xl md:text-5xl text-text-color font-bold mb-10">
            Welcome !
          </h1>
          <form className="flex flex-col" onSubmit={handleSubmit}>
            <div className="flex">
              <div className="flex flex-col gap-6">
                <div className="relative">
                  <UserIcon className="absolute left-5 top-1/2 -translate-y-1/2 h-8 w-8 text-text-color" />
                  <PrimaryInput
                      placeholder="Email or Username"
                      w="w-[270px] md:w-[500px]"
                      type="text"
                      name="usernameOrEmail"
                      value={formData.usernameOrEmail}
                      onChange={handleInputChange}
                  />
                </div>

                <div className="relative">
                  <LockClosedIcon className="absolute left-5 top-1/2 -translate-y-1/2 h-8 w-8 text-text-color" />
                  <PrimaryInput
                      placeholder="Password"
                      w="w-[270px] md:w-[500px]"
                      type="password"
                      name="password"
                      value={formData.password}
                      onChange={handleInputChange}
                  />
                </div>
              </div>
              {isLoading ? (
                <div className="md:ml-[50px] flex-shrink-0 mt-[50px] ml-[20px]">
                  <Spinner />
                </div>
              ) : (
                <button
                  className="md:ml-[50px] flex-shrink-0 mt-[20px] ml-[20px]"
                  style={{ animation: "bounceHorizontal 1s infinite" }}
                  type="submit"
                  disabled={isLoading}
                >
                  <ChevronRightIcon className="w-12 h-12 md:w-20 md:h-20 text-background-color bg-text-color rounded-full p-2" />
                </button>
              )}
            </div>
            <div className="mt-[34px] md:mt-8 flex flex-col md:flex-row md:justify-between md:w-[490px] gap-4">
              <a className="text-white font-semibold relative pb-1 group transition-all duration-300 hover:text-opacity-90">
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
                  <span className="absolute bottom-0 left-0 w-0 h-px bg-text-color transition-all duration-300 group-hover:w-full" />
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
