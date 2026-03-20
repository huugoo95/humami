import Link from "next/link";

export default function Home() {
  return (
    <main className="relative min-h-screen px-6 py-12 text-humami-text-base">
      <section className="mx-auto max-w-3xl text-center space-y-6 pt-8 md:pt-16">
        <h1 className="text-4xl md:text-6xl font-heading text-humami-text-heading">
          Redescubre la gastronomía
        </h1>
        <p className="text-lg md:text-xl font-body">
          Un viaje culinario que transforma lo cotidiano en memorable.
        </p>
        <div className="flex flex-wrap justify-center gap-3">
          <Link
            href="/meals"
            className="inline-block px-6 py-3 font-medium rounded-md bg-humami-accent-dark text-humami-bg transition hover:bg-humami-accent-dark"
          >
            Explorar comidas
          </Link>
          <Link
            href="/our-story"
            className="inline-block px-6 py-3 font-medium rounded-md border border-humami-accent-dark text-humami-accent-dark transition hover:bg-humami-accent-dark/5"
          >
            Conoce nuestra historia
          </Link>
        </div>
      </section>

      <section className="mx-auto mt-16 md:mt-24 max-w-3xl rounded-2xl border border-humami-text-base/10 bg-white/60 p-6 md:p-8 shadow-sm">
        <h2 className="text-2xl md:text-3xl font-heading text-humami-accent-dark mb-4">
          Sobre nosotros
        </h2>
        <p className="text-base md:text-lg leading-relaxed font-body">
          Humami nace de una mezcla muy personal: tecnología para ordenar lo complejo y cocina para disfrutar de lo
          esencial. Somos una marca construida desde la pasión por el producto, el sabor y la mesa compartida.
        </p>
        <p className="text-base md:text-lg leading-relaxed font-body mt-4">
          Estamos preparando esta sección con más detalle y una foto personal para contar mejor quién está detrás del
          proyecto.
        </p>
        <Link
          href="/our-story"
          className="inline-block mt-6 text-humami-accent-dark font-semibold hover:underline"
        >
          Leer historia completa →
        </Link>
      </section>
    </main>
  );
}
