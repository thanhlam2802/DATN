export const ErrorCodes = {
    userAlreadyExists: "AUTH:001:400",
    userNotFound: "AUTH:002:400",
    invalidCredentials: "AUTH:003:401",
    invalidPassword: "AUTH:004:401",
    invalidRole: "AUTH:005:403",
    passwordNotMatch: "AUTH:006:400",
    userNotVerified: "AUTH:007:400",
    userDeactivated: "AUTH:008:401",
    otpNotFound: "OTP:001:400",
    otpNotMatch: "OTP:002:400",
    otpExpired: "OTP:003:400",
};
