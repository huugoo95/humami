export const metadata = {
  title: "Privacidad",
  description: "Información de privacidad para solicitar guías de Humami.",
  alternates: { canonical: "/privacidad" },
};

export default function PrivacyPage() {
  return (
    <article className="mx-auto max-w-3xl rounded-2xl border border-humami-text-base/10 bg-white/70 p-7 shadow-sm sm:p-10">
      <p className="text-sm font-semibold uppercase tracking-[0.18em] text-humami-accent">Humami</p>
      <h1 className="mt-3 font-heading text-5xl text-humami-text-heading">Privacidad</h1>
      <div className="mt-8 space-y-7 leading-relaxed text-humami-text-base">
        <section>
          <h2 className="font-heading text-3xl text-humami-accent-dark">Guías y acceso por email</h2>
          <p className="mt-3">Cuando solicitas una guía, usamos tu email para darte acceso a su lectura y enviarte un enlace para volver a ella desde otro dispositivo.</p>
        </section>
        <section>
          <h2 className="font-heading text-3xl text-humami-accent-dark">Comunicaciones opcionales</h2>
          <p className="mt-3">Solo recibirás novedades, ideas o nuevas guías si marcas expresamente la casilla correspondiente. Esa elección no condiciona el acceso a la guía.</p>
        </section>
        <section>
          <h2 className="font-heading text-3xl text-humami-accent-dark">Datos que guardamos</h2>
          <p className="mt-3">Guardamos el email, la guía solicitada, la fecha de la solicitud y la versión de esta información aceptada. Estos datos nos permiten entregar el recurso y atender solicitudes relacionadas con él.</p>
        </section>
        <section>
          <h2 className="font-heading text-3xl text-humami-accent-dark">Cambios</h2>
          <p className="mt-3">Esta información se actualizará antes del lanzamiento público del sistema de guías. La versión aceptada quedará asociada a cada solicitud.</p>
        </section>
      </div>
    </article>
  );
}
