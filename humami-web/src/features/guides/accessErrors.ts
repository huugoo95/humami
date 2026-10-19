// Maps guide access API failures to actionable messages for the visitor.
export function guideAccessErrorMessage(status: number): string {
  if (status === 429) return "Has hecho varios intentos seguidos. Espera un minuto y vuelve a probar.";
  if (status === 400) return "Revisa que el email sea correcto y que hayas aceptado la política de privacidad.";
  if (status === 404) return "Esta guía no está disponible ahora mismo.";
  return "No hemos podido darte acceso ahora mismo. Inténtalo de nuevo en unos minutos.";
}
