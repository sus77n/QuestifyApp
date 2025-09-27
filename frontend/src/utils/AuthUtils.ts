export function isAuthenticated(): boolean {
  return !!localStorage.getItem("token");
}

export function getUserRole(): string | null {
  return localStorage.getItem("role");
}
