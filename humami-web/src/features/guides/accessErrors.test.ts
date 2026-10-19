import { guideAccessErrorMessage } from "./accessErrors";

describe("guide access error messages", () => {
  it("asks to wait when the request is rate limited", () => {
    expect(guideAccessErrorMessage(429)).toBe("Has hecho varios intentos seguidos. Espera un minuto y vuelve a probar.");
  });

  it("asks to check the email when the request is rejected", () => {
    expect(guideAccessErrorMessage(400)).toBe("Revisa que el email sea correcto y que hayas aceptado la política de privacidad.");
  });

  it("explains that the guide is unavailable when it does not exist", () => {
    expect(guideAccessErrorMessage(404)).toBe("Esta guía no está disponible ahora mismo.");
  });

  it("keeps a generic message for server errors", () => {
    expect(guideAccessErrorMessage(502)).toBe("No hemos podido darte acceso ahora mismo. Inténtalo de nuevo en unos minutos.");
  });
});
