export interface RefreshToken{
    type: string
    jti: string
    iat: Date
    exp: Date
}