import Link from "next/link";

export default function OurStoryPage() {
  return (
    <main className="mx-auto max-w-4xl py-8 md:py-12">
      <section className="rounded-2xl border border-humami-text-base/10 bg-white/70 p-6 md:p-10 shadow-sm">
        <p className="text-sm font-semibold tracking-wider uppercase text-humami-accent-dark mb-3">Sobre nosotros</p>

        <h1 className="text-3xl md:text-5xl font-heading text-humami-text-heading mb-6">
          La historia detrás de Humami
        </h1>

        <div className="space-y-5 text-base md:text-lg leading-relaxed font-body text-humami-text-base">
          <p>
            Humami nace de una mezcla muy personal: la cabeza de un developer y el corazón de una cocina de casa. Detrás
            del proyecto está Hugo, hijo de profesora de cocina, criado entre recetas, producto y respeto por el oficio.
          </p>
          <p>
            Con el tiempo, esa base gastronómica se juntó con la tecnología para construir una idea clara: hacer que
            cocinar bien sea más accesible, más ordenado y más inspirador, sin perder autenticidad.
          </p>
          <p>
            Desde una raíz gallega —donde el mar y el producto tienen un peso cultural enorme— Humami quiere unir método
            y sabor: estructura para simplificar decisiones, y cocina real para disfrutar mejor cada plato.
          </p>
        </div>

        <div className="mt-8 rounded-xl border border-dashed border-humami-text-base/25 p-4 md:p-5 bg-humami-bg-light/50">
          <p className="text-sm md:text-base font-body text-humami-text-base/85">
            📷 Aquí irá una foto personal para completar esta página. Cuando la tengas, la integramos en esta sección.
          </p>
        </div>

        <div className="mt-8 flex flex-wrap gap-3">
          <Link
            href="/meals"
            className="inline-block px-5 py-3 font-medium rounded-md bg-humami-accent-dark text-humami-bg transition hover:bg-humami-accent-dark"
          >
            Ver comidas
          </Link>
          <Link
            href="/"
            className="inline-block px-5 py-3 font-medium rounded-md border border-humami-accent-dark text-humami-accent-dark transition hover:bg-humami-accent-dark/5"
          >
            Volver al inicio
          </Link>
        </div>
      </section>
    </main>
  );
}
