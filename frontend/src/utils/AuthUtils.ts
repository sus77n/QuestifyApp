import {LoginResponseDTO} from "../model/AuthDTO";

export function isAuthenticated(): boolean {
  return !!localStorage.getItem("token");
}

export function getUserRole(): string | null {
  return localStorage.getItem("role");
}

export const setAuth = (data: LoginResponseDTO) => {
  localStorage.setItem("token", data.token);
  localStorage.setItem("id", data.id);
  localStorage.setItem("role", data.role);
  localStorage.setItem("username", data.username);
  localStorage.setItem("expiresAt",data.expiresAt)
};
