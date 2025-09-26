export interface UserDTO {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  token: string | null;
  tokenExpiration: string | null;
  createdAt: string | null;
  role: "STUDENT" | "TEACHER" | "ADMIN";
}
