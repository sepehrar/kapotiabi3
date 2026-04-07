export type RegisterFormData = {
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
};

export type RegisterValidationErrorKey =
  | "allFieldsRequired"
  | "nameTooShort"
  | "nameTooLong"
  | "invalidEmail"
  | "passwordTooShort"
  | "passwordsDoNotMatch"
  | "";

export function validateRegisterForm(
  form: RegisterFormData,
): RegisterValidationErrorKey {
  const trimmedName = form.name.trim();
  const trimmedEmail = form.email.trim();

  if (
    !trimmedName ||
    !trimmedEmail ||
    !form.password ||
    !form.confirmPassword
  ) {
    return "allFieldsRequired";
  }

  if (trimmedName.length < 2) {
    return "nameTooShort";
  }

  if (trimmedName.length > 80) {
    return "nameTooLong";
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(trimmedEmail)) {
    return "invalidEmail";
  }

  if (form.password.length < 6) {
    return "passwordTooShort";
  }

  if (form.password !== form.confirmPassword) {
    return "passwordsDoNotMatch";
  }

  return "";
}
