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
        <Link
          href="/meals"
          className={`
            inline-block px-6 py-3 font-medium rounded-md
            bg-humami-accent-dark
            text-humami-bg
            transition hover:bg-humami-accent-dark
          `}
        >
          Explorar comidas
        </Link>
      </section>

      <section className="mx-auto mt-16 md:mt-24 max-w-3xl rounded-2xl border border-humami-text-base/10 bg-white/60 p-6 md:p-8 shadow-sm">
        <h2 className="text-2xl md:text-3xl font-heading text-humami-text-heading mb-4">
          Sobre nosotros
        </h2>
        <p className="text-base md:text-lg leading-relaxed font-body">
          Humami nace de una mezcla muy personal: la cabeza de un developer y el corazón de una cocina de casa.
          Soy Hugo, crecí siendo hijo de profesora de cocina y aprendí pronto que cocinar no es solo seguir pasos,
          sino entender el producto, el tiempo y el cariño detrás de cada plato.
        </p>
        <p className="text-base md:text-lg leading-relaxed font-body mt-4">
          Este proyecto une esas dos partes de mí: tecnología para ordenar y simplificar, y gastronomía para disfrutar de verdad.
          Desde Galicia, con respeto por la cocina auténtica y la cultura del mar, Humami quiere ayudarte a cocinar mejor,
          comer mejor y vivir la mesa con más intención.
        </p>
      </section>
    </main>
  );
}
