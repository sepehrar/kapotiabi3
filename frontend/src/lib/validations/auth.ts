// frontend\src\lib\validations\auth.ts
// register
export type RegisterFormData = {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  phone: string;
};

export type RegisterValidationErrorKey =
  | "allFieldsRequired"
  | "firstNameTooShort"
  | "lastNameTooShort"
  | "invalidEmail"
  | "invalidPhone"
  | "passwordTooShort"
  | "passwordsDoNotMatch"
  | "";

export function validateRegisterForm(
  form: RegisterFormData,
): RegisterValidationErrorKey {
  const firstName = form.firstName.trim();
  const lastName = form.lastName.trim();
  const email = form.email.trim();
  const phone = form.phone.trim();

  if (
    !firstName ||
    !lastName ||
    !email ||
    !form.password ||
    !form.confirmPassword
  ) {
    return "allFieldsRequired";
  }

  if (firstName.length < 2) {
    return "firstNameTooShort";
  }

  if (lastName.length < 2) {
    return "lastNameTooShort";
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return "invalidEmail";
  }

  if (phone) {
    const phoneRegex = /^[+\d\s\-()]+$/;
    const digitsOnly = phone.replace(/\D/g, "");

    if (!phoneRegex.test(phone) || digitsOnly.length < 7) {
      return "invalidPhone";
    }
  }

  if (form.password.length < 6) {
    return "passwordTooShort";
  }

  if (form.password !== form.confirmPassword) {
    return "passwordsDoNotMatch";
  }

  return "";
}

// login
export type LoginFormData = {
  email: string;
  password: string;
};

export type LoginValidationErrorKey = "allFieldsRequired" | "invalidEmail" | "";

export function validateLoginForm(
  form: LoginFormData,
): LoginValidationErrorKey {
  const email = form.email.trim();

  if (!email || !form.password) {
    return "allFieldsRequired";
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return "invalidEmail";
  }

  return "";
}
