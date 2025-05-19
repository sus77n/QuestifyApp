export interface LoginDTO {
    email: string;
    password: string;
}

export interface LoginResponseDTO {
    token: string;
    issuedAt: string;
    expiresAt: string;
    username: string;
}

export interface SignupDTO {
    email: string;
    password: string;
    username: string;
    role: 'Student' | 'Teacher' | 'Administrator';
}