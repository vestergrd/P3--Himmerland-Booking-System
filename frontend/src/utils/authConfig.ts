export type UserRole = "ROLE_TENANT" | "ROLE_ADMIN";

export const defaultHomePages: Record<UserRole, string> = {
  ROLE_TENANT: "/hjem",
  ROLE_ADMIN: "/admin-overblik",
};

export const getUserRole = (): UserRole | null => {
  const role = document.cookie
    .split("; ")
    .find((row) => row.startsWith("authIndicator="))
    ?.split("=")[1] as UserRole;
  return role || null;
};
