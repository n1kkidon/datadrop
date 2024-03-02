export interface Token{
    sub: string
    iat: Date
    exp: Date
    jti: string
    roles: Roles[]
}

export interface Roles{
    authority: string
}