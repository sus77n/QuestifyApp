export interface LoginDTO {
    usernameOrEmail: string;
    password: string;
}

export interface LoginResponseDTO {
    token: string;
    id: string;
    issuedAt: string;
    expiresAt: string;
    username: string;
    role: 'STUDENT' | 'TEACHER' | 'ADMIN';
}

export interface SignupDTO {
    email: string;
    password: string;
    username?: string;
    role?: 'STUDENT' | 'TEACHER' | 'ADMIN';
}