export interface UserDTO {
    id: number;
    username: string;
    password: string;
    email: string;
    role: 'STUDENT' | 'TEACHER' | 'ADMIN';
}